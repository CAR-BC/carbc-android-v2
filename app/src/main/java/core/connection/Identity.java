package core.connection;

public class Identity {
    private String block_hash;
    private String public_key;
    private String role;
    private String name;

    public Identity(String block_hash, String public_key, String role, String name){
        this.block_hash = block_hash;
        this.role = role;
        this.name = name;
        this.public_key = public_key;
    }

    public String getBlock_hash() {
        return block_hash;
    }

    public String getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    public String getPublic_key() {
        return public_key;
    }
}
