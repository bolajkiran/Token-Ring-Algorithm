import java.io.Serializable;

public class TokenRingFrame implements Serializable  {
    private static final long serialVersionUID = 1001626623L;
    private String process_id;
    private int pid;
    private boolean tokenOwnership;
    private int count;

    public String getProcess_id() {
        return process_id;
    }

    public void setProcess_id(String process_id) {
        this.process_id = process_id;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public boolean isTokenOwnership() {
        return tokenOwnership;
    }

    public void setTokenOwnership(boolean tokenOwnership) {
        this.tokenOwnership = tokenOwnership;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

