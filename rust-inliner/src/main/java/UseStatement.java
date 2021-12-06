import java.util.Arrays;

public class UseStatement {
    String[] mods;
    String alias;

    public UseStatement(String content){
        String[] split = content.split(" as ");
        if(split.length > 1){
            alias = split[1].trim();
            content = split[0];
        }
        mods = content.replaceAll("\\s+", "").split("::", -1);
        if(mods[mods.length - 1].equals("self")){
            mods = Arrays.copyOf(mods, mods.length - 1);
        }
    }

    public String concatMods(){
        return String.join("::", mods);
    }

    public String[] getMods() {
        return mods;
    }

    public void setMods(String[] mods) {
        this.mods = mods;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
