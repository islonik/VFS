package org.vfs.core.network.protocol;

/**
 * @author Lipatov Nikita
 */
public interface Response
{
    public static final int STATUS_OK = 0;
    public static final int STATUS_FAIL = 1;
    public static final int STATUS_SUCCESS_CONNECT = 2;
    public static final int STATUS_FAIL_CONNECT = 3;
    public static final int STATUS_SUCCESS_QUIT = 4;
    public static final int STATUS_FAIL_QUIT = 5;

    public int getCode();
    public long getSpecificCode();
    public String getMessage();

    public void parse(String data);

    public String toXml();
}
