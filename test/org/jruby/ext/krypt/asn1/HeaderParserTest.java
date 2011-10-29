/***** BEGIN LICENSE BLOCK *****
* Version: CPL 1.0/GPL 2.0/LGPL 2.1
*
* The contents of this file are subject to the Common Public
* License Version 1.0 (the "License"); you may not use this file
* except in compliance with the License. You may obtain a copy of
* the License at http://www.eclipse.org/legal/cpl-v10.html
*
* Software distributed under the License is distributed on an "AS
* IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
* implied. See the License for the specific language governing
* rights and limitations under the License.
*
* Copyright (C) 2011 Martin Bosslet <Martin.Bosslet@googlemail.com>
*
* Alternatively, the contents of this file may be used under the terms of
* either of the GNU General Public License Version 2 or later (the "GPL"),
* or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
* in which case the provisions of the GPL or the LGPL are applicable instead
* of those above. If you wish to allow use of your version of this file only
* under the terms of either the GPL or the LGPL, and not to allow others to
* use your version of this file under the terms of the CPL, indicate your
* decision by deleting the provisions above and replace them with the notice
* and other provisions required by the GPL or the LGPL. If you do not delete
* the provisions above, a recipient may use your version of this file under
* the terms of any one of the CPL, the GPL or the LGPL.
 */
package org.jruby.ext.krypt.asn1;

import org.jruby.ext.krypt.asn1.resources.Resources;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * 
 * @author <a href="mailto:Martin.Bosslet@googlemail.com">Martin Bosslet</a>
 */
public class HeaderParserTest {
    
    @Test
    public void parseTokensSkippingTopLevel() throws Exception {
        InputStream in = Resources.certificate();
        ParsedHeader token;
        int numTokens = 0;

        try {
            Parser p = new ParserFactory().newHeaderParser(in);
            while ((token = p.next()) != null) {
                numTokens++;
                token.skipValue();
            }
            assertEquals(1, numTokens);
        }
        finally {
            in.close();
        }
    }

    @Test
    public void parseTokensSkipping() throws Exception {
        InputStream in = Resources.certificate();
        ParsedHeader token;
        int numTokens = 0;

        try {
            Parser p = new ParserFactory().newHeaderParser(in);
            while ((token = p.next()) != null) {
                numTokens++;
                if (token.isConstructed())
                    continue;
                token.skipValue();
            }
            assertTrue(numTokens > 1);
        }
        finally {
            in.close();
        }
    }
    
    @Test
    public void consumeTopLevelStream() throws Exception {
        InputStream in = Resources.certificate();
        ParsedHeader token;
        int numTokens = 0;

        try {
            Parser p = new ParserFactory().newHeaderParser(Resources.certificate());
            while ((token = p.next()) != null) {
                numTokens++;
                consumeTokenStream(token); //need to consume the value bytes
            }
            assertEquals(1, numTokens);
        }
        finally {
            in.close();
        }
    }

    @Test
    public void consumeAllStreams() throws Exception {
        InputStream in = Resources.certificate();
        ParsedHeader token;
        int numTokens = 0;

        try {
            Parser p = new ParserFactory().newHeaderParser(Resources.certificate());
            while ((token = p.next()) != null) {
                numTokens++;
                if (token.isConstructed())
                    continue;
                consumeTokenStream(token); //need to consume the value bytes
            }
            assertTrue(numTokens > 1);
        }
        finally {
            in.close();
        }
    }
    
    private static void consumeTokenStream(ParsedHeader token) {
        InputStream in = token.getValueStream();

        try {
            byte[] buf = new byte[8192];
            while (in.read(buf) != -1) {}
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        finally {
            try {
                in.close();
            }
            catch (IOException ex) {
                //silent
            }
        }
    }
    
    @Test
    public void parseTokensAndTestMethods() throws Exception {
        InputStream in = Resources.certificate();
        int numTokens = 0;
        ParsedHeader token;

        try {
            Parser p = new ParserFactory().newHeaderParser(Resources.certificate());
            while ((token = p.next()) != null) {
                numTokens++;
                token.getLength();
                assertNotNull(token.getTagClass());
                token.getTag();
                token.isInfiniteLength();
                if (token.isConstructed())
                    continue;
                //consumes primitive value
                assertNotNull(token.getValue());
            }
        }
        finally {
            in.close();
        }
    }
}