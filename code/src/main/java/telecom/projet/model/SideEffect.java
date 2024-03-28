package telecom.projet.model;

import java.util.Objects;

public class SideEffect {

    private String name;
    private String cid;

    private String cui;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SideEffect that = (SideEffect) o;
        return Objects.equals(name, that.name) && Objects.equals(cid, that.cid) && Objects.equals(cui, that.cui);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, cid, cui);
    }

    public String getCui() {
        return cui;
    }

    public void setCui(String cui) {
        this.cui = cui;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }
}
