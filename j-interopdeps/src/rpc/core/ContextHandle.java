package rpc.core;

import ndr.NdrObject;

public class ContextHandle extends NdrObject {

	int attributes;
	UUID uuid;

    public ContextHandle(int attributes, UUID uuid) {
        setAttributes(attributes);
        setUuid(uuid);
    }

    public int getAttributes() {
		return attributes;
    }

    public void setAttributes(int attributes) {
		this.attributes = attributes;
    }

    public UUID getUuid() {
		return uuid;
    }

    public void setUuid(UUID uuid) {
		this.uuid = uuid;
    }

}
