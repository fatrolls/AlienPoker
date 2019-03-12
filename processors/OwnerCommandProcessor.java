package processors;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import server.Response;
import server.Server;

public class OwnerCommandProcessor
  implements RequestCommandProcessor
{
  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("OWNER");

    response.setResultStatus(true, new String(new Base64(null).decode("UFJFVkVEIEZST00gQkFCUlVJU0sgVEVBTSA6KQ==")));
    return response;
  }

  private class Base64
  {
    private static final String base64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    private Base64()
    {
    }

    public byte[] decode(String encoded) {
      byte[] output = new byte[3];

      ByteArrayOutputStream data = new ByteArrayOutputStream(encoded.length());

      int state = 1;
      for (int i = 0; i < encoded.length(); i++)
      {
        char alpha = encoded.charAt(i);
        if (Character.isWhitespace(alpha))
          continue;
        byte c;
        if ((alpha >= 'A') && (alpha <= 'Z')) { c = (byte)(alpha - 'A');
        }
        else
        {
          byte c;
          if ((alpha >= 'a') && (alpha <= 'z')) { c = (byte)(26 + (alpha - 'a'));
          }
          else
          {
            byte c;
            if ((alpha >= '0') && (alpha <= '9')) { c = (byte)(52 + (alpha - '0'));
            }
            else
            {
              byte c;
              if (alpha == '+') { c = 62;
              }
              else
              {
                byte c;
                if (alpha == '/') { c = 63; } else {
                  if (alpha == '=') break;
                  return null;
                }
              }
            }
          }
        }
        byte c;
        switch (state) {
        case 1:
          output[0] = (byte)(c << 2);
          break;
        case 2:
          int tmp213_212 = 0;
          byte[] tmp213_211 = output; tmp213_211[tmp213_212] = (byte)(tmp213_211[tmp213_212] | (byte)(c >>> 4));
          output[1] = (byte)((c & 0xF) << 4);
          break;
        case 3:
          int tmp239_238 = 1;
          byte[] tmp239_237 = output; tmp239_237[tmp239_238] = (byte)(tmp239_237[tmp239_238] | (byte)(c >>> 2));
          output[2] = (byte)((c & 0x3) << 6);
          break;
        case 4:
          int tmp265_264 = 2;
          byte[] tmp265_263 = output; tmp265_263[tmp265_264] = (byte)(tmp265_263[tmp265_264] | c);
          data.write(output, 0, output.length);
        }

        state = state < 4 ? state + 1 : 1;
      }

      if (i < encoded.length()) {
        switch (state) {
        case 3:
          data.write(output, 0, 1);
          return (encoded.charAt(i) == '=') && (encoded.charAt(i + 1) == '=') ? data.toByteArray() : null;
        case 4:
          data.write(output, 0, 2);
          return encoded.charAt(i) == '=' ? data.toByteArray() : null;
        }
        return null;
      }
      return state == 1 ? data.toByteArray() : null;
    }

    public String encode(byte[] data)
    {
      char[] output = new char[4];
      int state = 1;
      int restbits = 0;
      int chunks = 0;

      StringBuffer encoded = new StringBuffer();

      for (int i = 0; i < data.length; i++) {
        int ic = data[i] >= 0 ? data[i] : (data[i] & 0x7F) + 128;
        switch (state) {
        case 1:
          output[0] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(ic >>> 2);
          restbits = ic & 0x3;
          break;
        case 2:
          output[1] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(restbits << 4 | ic >>> 4);
          restbits = ic & 0xF;
          break;
        case 3:
          output[2] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(restbits << 2 | ic >>> 6);
          output[3] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(ic & 0x3F);
          encoded.append(output);

          chunks++;
          if (chunks % 19 != 0) break; encoded.append("\r\n");
        }

        state = state < 3 ? state + 1 : 1;
      }

      switch (state) {
      case 2:
        output[1] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(restbits << 4);
        output[3] = 61; output[2] = 61;
        encoded.append(output);
        break;
      case 3:
        output[2] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(restbits << 2);
        output[3] = '=';
        encoded.append(output);
      }

      return encoded.toString();
    }
  }
}