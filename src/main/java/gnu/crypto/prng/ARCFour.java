package gnu.crypto.prng;

// ----------------------------------------------------------------------------
// $Id: ARCFour.java,v 1.2 2003/04/28 10:56:22 raif Exp $
//
// Copyright (C) 2002, 2003 Free Software Foundation, Inc.
//
// This file is part of GNU Crypto.
//
// GNU Crypto is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2, or (at your option)
// any later version.
//
// GNU Crypto is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; see the file COPYING.  If not, write to the
//
//    Free Software Foundation Inc.,
//    59 Temple Place - Suite 330,
//    Boston, MA 02111-1307
//    USA
//
// Linking this library statically or dynamically with other modules is
// making a combined work based on this library.  Thus, the terms and
// conditions of the GNU General Public License cover the whole
// combination.
//
// As a special exception, the copyright holders of this library give
// you permission to link this library with independent modules to
// produce an executable, regardless of the license terms of these
// independent modules, and to copy and distribute the resulting
// executable under terms of your choice, provided that you also meet,
// for each linked independent module, the terms and conditions of the
// license of that module.  An independent module is a module which is
// not derived from or based on this library.  If you modify this
// library, you may extend this exception to your version of the
// library, but you are not obligated to do so.  If you do not wish to
// do so, delete this exception statement from your version.
// ----------------------------------------------------------------------------
import java.util.Map;

/**
 * RC4 is a stream cipher developed by Ron Rivest. Until 1994 RC4 was a trade
 * secret of RSA Data Security, Inc., when it was released anonymously to a
 * mailing list. This version is a descendent of that code, and since there is
 * no proof that the leaked version was in fact RC4 and because "RC4" is a
 * trademark, it is called "ARCFOUR", short for "Allegedly RC4".
 *
 * <p>
 * This class only implements the <i>keystream</i> of ARCFOUR. To use this as a
 * stream cipher, one would say:</p>
 *
 * <pre>    out = in ^ arcfour.nextByte();</pre>
 *
 * <p>
 * This operation works for encryption and decryption.</p>
 *
 * <p>
 * References:</p>
 *
 * <ol>
 * <li>Schneier, Bruce: <i>Applied Cryptography: Protocols, Algorithms, and
 * Source Code in C, Second Edition.</i> (1996 John Wiley and Sons), pp.
 * 397--398. ISBN 0-471-11709-9</li>
 * <li>K. Kaukonen and R. Thayer, "A Stream Cipher Encryption Algorithm
 * 'Arcfour'", Internet Draft (expired), <a
 * href="http://www.mozilla.org/projects/security/pki/nss/draft-kaukonen-cipher-arcfour-03.txt">draft-kaukonen-cipher-arcfour-03.txt</a></li>
 * </ol>
 *
 * @version $Revision: 1.2 $
 */
public class ARCFour extends BasePRNG {

    // Constants and variables.
    // -----------------------------------------------------------------------
    /**
     * The attributes property name for the key bytes.
     */
    public static final String ARCFOUR_KEY_MATERIAL = "gnu.crypto.prng.arcfour.key-material";

    /**
     * The size of the internal S-box.
     */
    public static final int ARCFOUR_SBOX_SIZE = 256;

    /**
     * The S-box.
     */
    private byte[] s;

    private byte m, n;

    // Constructors.
    // -----------------------------------------------------------------------
    /**
     * Default 0-arguments constructor.
     */
    public ARCFour() {
        super("arcfour");
    }

    // Methods implementing BasePRNG.
    // -----------------------------------------------------------------------
    @Override
    public Object clone() {
        ARCFour copy = new ARCFour();
        copy.s = (s != null) ? s.clone() : null;
        copy.m = m;
        copy.n = n;
        copy.buffer = (buffer != null) ? buffer.clone() : null;
        copy.ndx = ndx;
        copy.initialised = initialised;
        return copy;
    }

    @Override
    public void setup(Map attributes) {
        byte[] kb = (byte[]) attributes.get(ARCFOUR_KEY_MATERIAL);

        if (kb == null) {
            throw new IllegalArgumentException("ARCFOUR needs a key");
        }

        s = new byte[ARCFOUR_SBOX_SIZE];
        m = n = 0;
        byte[] k = new byte[ARCFOUR_SBOX_SIZE];

        for (int i = 0; i < ARCFOUR_SBOX_SIZE; i++) {
            s[i] = (byte) i;
        }

        if (kb.length > 0) {
            for (int i = 0, j = 0; i < ARCFOUR_SBOX_SIZE; i++) {
                k[i] = kb[j++];
                if (j >= kb.length) {
                    j = 0;
                }
            }
        }

        for (int i = 0, j = 0; i < ARCFOUR_SBOX_SIZE; i++) {
            j = j + s[i] + k[i];
            byte temp = s[i];
            s[i] = s[j & 0xff];
            s[j & 0xff] = temp;
        }

        buffer = new byte[ARCFOUR_SBOX_SIZE];
        fillBlock();
    }

    @Override
    public void fillBlock() {
        for (int i = 0; i < buffer.length; i++) {
            m++;
            n = (byte) (n + s[m & 0xff]);
            byte temp = s[m & 0xff];
            s[m & 0xff] = s[n & 0xff];
            s[n & 0xff] = temp;
            temp = (byte) (s[m & 0xff] + s[n & 0xff]);
            buffer[i] = s[temp & 0xff];
        }
    }
}
