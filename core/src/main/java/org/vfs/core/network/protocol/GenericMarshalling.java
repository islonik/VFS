package org.vfs.core.network.protocol;

import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lipatov Nikita
 */
public class GenericMarshalling {
    private static Map<String, String> mapping = null;

    public static ByteBuffer objectToByteBuffer(Object o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Message message = (Message) o;
        byte[] name = message.getDescriptorForType().getFullName().getBytes("UTF-8");
        baos.write(name.length); // TODO: Length as int and not byte
        // Write the full descriptor name, i.e. protobuf.Person
        baos.write(name);
        byte[] messageBytes = message.toByteArray();
        baos.write(messageBytes.length); // TODO: Length as int and not byte
        baos.write(messageBytes);
        return ByteBuffer.wrap(baos.toByteArray());
    }

    public static Object objectFromByteBuffer(byte[] buffer) throws Exception {
        initMapping();
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        byte[] name = new byte[bais.read()];
        bais.read(name); // TODO: Read fully??
        // Get the class name associated with the descriptor name
        String className = mapping.get(new String(name, "UTF-8"));
        Class clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
        Method parseFromMethod = clazz.getMethod("parseFrom", byte[].class);
        byte[] message = new byte[bais.read()];
        bais.read(message); // TODO: Read fully??
        return parseFromMethod.invoke(null, message);
    }

    private static void initMapping() throws IOException, Descriptors.DescriptorValidationException {
        if(mapping == null) {
            synchronized (GenericMarshalling.class) {
                if(mapping == null) {
                    mapping = new HashMap<>();
                    FileDescriptorSet descriptorSet = FileDescriptorSet.parseFrom(
                            new FileInputStream("core/src/main/resources/protocol.desc")
                    );

                    for (FileDescriptorProto fdp: descriptorSet.getFileList()) {
                        FileDescriptor fd = FileDescriptor.buildFrom(
                                fdp,
                                new FileDescriptor[]{}
                        );

                        for (Descriptor descriptor : fd.getMessageTypes()) {
                            String className =
                                      fdp.getOptions().getJavaPackage() + "."
                                    + fdp.getOptions().getJavaOuterClassname() + "$"
                                    + descriptor.getName();
                            mapping.put(descriptor.getFullName(), className);
                        }
                    }
                }
            }
        }
    }
}
