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

package com.cburch.logisim.util;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.cburch.logisim.data.Direction;

public class Icons {
  public static Icon getIcon(String name) {
    java.net.URL url = Icons.class.getClassLoader().getResource(
        path + "/" + name);
    if (url == null)
      return null;
    return new ImageIcon(url);
  }

  public static void paintRotated(Graphics g, int x, int y, Direction dir,
      Icon icon, Component dest) {
    if (dir == Direction.EAST) {
      icon.paintIcon(dest, g, x, y);
      return;
    }

    Graphics2D g2 = (Graphics2D) g.create();
    double cx = x + icon.getIconWidth() / 2.0;
    double cy = y + icon.getIconHeight() / 2.0;
    if (dir == Direction.WEST) {
      g2.rotate(Math.PI, cx, cy);
    } else if (dir == Direction.NORTH) {
      g2.rotate(-Math.PI / 2.0, cx, cy);
    } else if (dir == Direction.SOUTH) {
      g2.rotate(Math.PI / 2.0, cx, cy);
    } else {
      g2.translate(-x, -y);
    }
    icon.paintIcon(dest, g2, x, y);
    g2.dispose();
  }

  private static final String path = "resources/logisim/icons";

  private Icons() {
  }
}
