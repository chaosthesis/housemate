package housemate.src.housemate.entitlement.credential;

public class VoicePrint extends Credential {
    private String type;
    private String voicePrint;

    public VoicePrint(String key) {
        this.type = "voice_print";
        this.voicePrint = key;
    }

    public String getType() {
        return type;
    }
    
    public String getKey() {
        return voicePrint;
    }

    @Override
    public void print() {
        System.out.println("VoicePrint: " + voicePrint);
    }
}