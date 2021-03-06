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

package com.cburch.logisim.std.arith;
import static com.cburch.logisim.std.Strings.S;

import com.bfh.logisim.hdlgenerator.HDLSupport;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeOption;
import com.cburch.logisim.data.Attributes;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.instance.Instance;
import com.cburch.logisim.instance.InstanceFactory;
import com.cburch.logisim.instance.InstancePainter;
import com.cburch.logisim.instance.InstanceState;
import com.cburch.logisim.instance.Port;
import com.cburch.logisim.instance.StdAttr;
import com.cburch.logisim.tools.key.BitWidthConfigurator;

public class Comparator extends InstanceFactory {
  public static final AttributeOption SIGNED_OPTION = new AttributeOption(
      "twosComplement", "twosComplement",
      S.getter("twosComplementOption"));
  public static final AttributeOption UNSIGNED_OPTION = new AttributeOption(
      "unsigned", "unsigned", S.getter("unsignedOption"));
  public static final Attribute<AttributeOption> MODE_ATTRIBUTE = Attributes
      .forOption("mode", S.getter("comparatorType"),
          new AttributeOption[] { SIGNED_OPTION, UNSIGNED_OPTION });

  static final int IN0 = 0;
  static final int IN1 = 1;
  static final int GT = 2;
  static final int EQ = 3;
  static final int LT = 4;

  public Comparator() {
    super("Comparator", S.getter("comparatorComponent"));
    setAttributes(new Attribute[] { StdAttr.WIDTH, MODE_ATTRIBUTE },
        new Object[] { BitWidth.create(8), SIGNED_OPTION });
    setKeyConfigurator(new BitWidthConfigurator(StdAttr.WIDTH));
    setOffsetBounds(Bounds.create(-40, -20, 40, 40));
    setIconName("comparator.gif");

    Port[] ps = new Port[5];
    ps[IN0] = new Port(-40, -10, Port.INPUT, StdAttr.WIDTH);
    ps[IN1] = new Port(-40, 10, Port.INPUT, StdAttr.WIDTH);
    ps[GT] = new Port(0, -10, Port.OUTPUT, 1);
    ps[EQ] = new Port(0, 0, Port.OUTPUT, 1);
    ps[LT] = new Port(0, 10, Port.OUTPUT, 1);
    ps[IN0].setToolTip(S.getter("comparatorInputATip"));
    ps[IN1].setToolTip(S.getter("comparatorInputBTip"));
    ps[GT].setToolTip(S.getter("comparatorGreaterTip"));
    ps[EQ].setToolTip(S.getter("comparatorEqualTip"));
    ps[LT].setToolTip(S.getter("comparatorLessTip"));
    setPorts(ps);
  }

  //
  // methods for instances
  //
  @Override
  protected void configureNewInstance(Instance instance) {
    instance.addAttributeListener();
  }

  @Override
  public HDLSupport getHDLSupport(HDLSupport.ComponentContext ctx) {
    return new ComparatorHDLGenerator(ctx);
  }

  @Override
  protected void instanceAttributeChanged(Instance instance, Attribute<?> attr) {
    instance.fireInvalidated();
  }

  @Override
  public void paintInstance(InstancePainter painter) {
    painter.drawBounds();
    painter.drawPort(IN0);
    painter.drawPort(IN1);
    painter.drawPort(GT, ">", Direction.WEST);
    painter.drawPort(EQ, "=", Direction.WEST);
    painter.drawPort(LT, "<", Direction.WEST);
  }

  @Override
  public void propagate(InstanceState state) {
    // get attributes
    BitWidth dataWidth = state.getAttributeValue(StdAttr.WIDTH);

    // compute outputs
    Value gt = Value.FALSE;
    Value eq = Value.TRUE;
    Value lt = Value.FALSE;

    Value a = state.getPortValue(IN0);
    Value b = state.getPortValue(IN1);
    Value[] ax = a.getAll();
    Value[] bx = b.getAll();
    int maxlen = Math.max(ax.length, bx.length);
    for (int pos = maxlen - 1; pos >= 0; pos--) {
      Value ab = pos < ax.length ? ax[pos] : Value.ERROR;
      Value bb = pos < bx.length ? bx[pos] : Value.ERROR;
      if (pos == ax.length - 1 && ab != bb) {
        Object mode = state.getAttributeValue(MODE_ATTRIBUTE);
        if (mode != UNSIGNED_OPTION) {
          Value t = ab;
          ab = bb;
          bb = t;
        }
      }

      if (ab == Value.ERROR || bb == Value.ERROR) {
        gt = Value.ERROR;
        eq = Value.ERROR;
        lt = Value.ERROR;
        break;
      } else if (ab == Value.UNKNOWN || bb == Value.UNKNOWN) {
        gt = Value.UNKNOWN;
        eq = Value.UNKNOWN;
        lt = Value.UNKNOWN;
        break;
      } else if (ab != bb) {
        eq = Value.FALSE;
        if (ab == Value.TRUE)
          gt = Value.TRUE;
        else
          lt = Value.TRUE;
        break;
      }
    }

    // propagate them
    int delay = (dataWidth.getWidth() + 2) * Adder.PER_DELAY;
    state.setPort(GT, gt, delay);
    state.setPort(EQ, eq, delay);
    state.setPort(LT, lt, delay);
  }
}
