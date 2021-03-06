/**
 * This file is part of Logisim-evolution.
 *
 * Logisim-evolution is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Logisim-evolution is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with Logisim-evolution.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Original code by Carl Burch (http://www.cburch.com), 2011.
 * Subsequent modifications by:
 *   + Haute École Spécialisée Bernoise
 *     http://www.bfh.ch
 *   + Haute École du paysage, d'ingénierie et d'architecture de Genève
 *     http://hepia.hesge.ch/
 *   + Haute École d'Ingénierie et de Gestion du Canton de Vaud
 *     http://www.heig-vd.ch/
 *   + REDS Institute - HEIG-VD, Yverdon-les-Bains, Switzerland
 *     http://reds.heig-vd.ch
 * This version of the project is currently maintained by:
 *   + Kevin Walsh (kwalsh@holycross.edu, http://mathcs.holycross.edu/~kwalsh)
 */

package com.cburch.draw.actions;
import static com.cburch.draw.Strings.S;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.cburch.draw.model.AttributeMapKey;
import com.cburch.draw.model.CanvasModel;
import com.cburch.draw.model.CanvasObject;
import com.cburch.logisim.data.Attribute;

public class ModelChangeAttributeAction extends ModelAction {
	private Map<AttributeMapKey, Object> oldValues;
	private Map<AttributeMapKey, Object> newValues;
	private Attribute<?> attr;

	public ModelChangeAttributeAction(CanvasModel model,
			Map<AttributeMapKey, Object> oldValues,
			Map<AttributeMapKey, Object> newValues) {
		super(model);
		this.oldValues = oldValues;
		this.newValues = newValues;
	}

	@Override
	void doSub(CanvasModel model) {
		model.setAttributeValues(newValues);
	}

	@Override
	public String getName() {
		Attribute<?> a = attr;
		if (a == null) {
			boolean found = false;
			for (AttributeMapKey key : newValues.keySet()) {
				Attribute<?> at = key.getAttribute();
				if (found) {
					if (a == null ? at != null : !a.equals(at)) {
						a = null;
						break;
					}
				} else {
					found = true;
					a = at;
				}
			}
			attr = a;
		}
		if (a == null) {
			return S.get("actionChangeAttributes");
		} else {
			return S.fmt("actionChangeAttribute", a.getDisplayName());
		}
	}

	@Override
	public Collection<CanvasObject> getObjects() {
		Set<CanvasObject> ret = new HashSet<CanvasObject>();
		for (AttributeMapKey key : newValues.keySet()) {
			ret.add(key.getObject());
		}
		return ret;
	}

	@Override
	void undoSub(CanvasModel model) {
		model.setAttributeValues(oldValues);
	}
}
