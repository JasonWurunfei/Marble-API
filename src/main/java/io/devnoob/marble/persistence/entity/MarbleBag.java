package io.devnoob.marble.persistence.entity;

public class MarbleBag {
    private Long id;
    private Long marbleId;
    private Long bagId;

    public MarbleBag(Long id, Long marbleId, Long bagId) {
        this.id = id;
        this.marbleId = marbleId;
        this.bagId = bagId;
    }

    public MarbleBag(Long marbleId, Long bagId) {
        this.marbleId = marbleId;
        this.bagId = bagId;
    }
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getMarbleId() {
        return marbleId;
    }
    public void setMarbleId(Long marbleId) {
        this.marbleId = marbleId;
    }
    public Long getBagId() {
        return bagId;
    }
    public void setBagId(Long bagId) {
        this.bagId = bagId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bagId == null) ? 0 : bagId.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((marbleId == null) ? 0 : marbleId.hashCode());
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
        MarbleBag other = (MarbleBag) obj;
        if (bagId == null) {
            if (other.bagId != null)
                return false;
        } else if (!bagId.equals(other.bagId))
            return false;
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
        return true;
    }
}
