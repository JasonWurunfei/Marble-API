package io.devnoob.marble.persistence.entity;

public class Impression {
    private Long id;
    private String path;
    private Long marbleId;
    private int type;
    
    public Impression(Long id, String path, Long marbleId, int type) {
        this.id = id;
        this.path = path;
        this.marbleId = marbleId;
        this.type = type;
    }

    public Impression(String path, Long marbleId, int type) {
        this.path = path;
        this.marbleId = marbleId;
        this.type = type;
    }
    
    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getMarbleId() {
        return marbleId;
    }

    public void setMarbleId(Long marbleId) {
        this.marbleId = marbleId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
 
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((marbleId == null) ? 0 : marbleId.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + type;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Impression other = (Impression) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (marbleId == null) {
            if (other.marbleId != null)
                return false;
        } else if (!marbleId.equals(other.marbleId))
            return false;
        if (path == null) {
            if (other.path != null)
                return false;
        } else if (!path.equals(other.path))
            return false;
        if (type != other.type)
            return false;
        return true;
    }
}
