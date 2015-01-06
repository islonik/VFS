package org.vfs.core.network.protocol;

import com.google.protobuf.DescriptorProtos;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author Lipatov Nikita
 */
public class GenericMarshallingTest {

    @Test
    public void test () throws IOException {
        DescriptorProtos.FileDescriptorSet descriptorSet = DescriptorProtos.FileDescriptorSet.parseFrom(
                new FileInputStream("core/src/main/resources/protocol.desc")
        );
        System.out.println(descriptorSet.toString());
    }

}
