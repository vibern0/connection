

import java.io.Serializable;

public class TcpPayload implements Serializable
{
    static final long serialVersionUID = -50077493051991107L;

    private final int value;

    public TcpPayload()
    {
        this.value = 123;
    }

    @Override
    public String toString()
    {
        String output = "Value = " + Integer.toString(this.value);
        return output;
    }
}
