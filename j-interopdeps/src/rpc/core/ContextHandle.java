/* Donated by Jarapac (http://jarapac.sourceforge.net/)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110, USA
 */

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
