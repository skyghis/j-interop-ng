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



package rpc;

import rpc.core.PresentationResult;

public class PresentationException extends BindException {

    /**
	 *
	 */
	private static final long serialVersionUID = 5421952951585948361L;

	public PresentationException() {
        super();
    }

    public PresentationException(String message) {
        super(message);
    }

    public PresentationException(String message, PresentationResult result) {
        super(message(message, result));
    }

    private static String message(String message, PresentationResult result) {
        if (result == null) return message;
        return (message != null) ? message + " (" + message(result) + ")" :
                message(result);
    }

    private static String message(PresentationResult result) {
        StringBuffer message = new StringBuffer();
        switch (result.result) {
        case PresentationResult.ACCEPTANCE:
            message.append("ACCEPTANCE");
            break;
        case PresentationResult.USER_REJECTION:
            message.append("USER_REJECTION");
            break;
        case PresentationResult.PROVIDER_REJECTION:
            message.append("PROVIDER_REJECTION");
            break;
        default:
            message.append("unknown");
        }
        message.append("; ");
        switch (result.reason) {
        case PresentationResult.REASON_NOT_SPECIFIED:
            message.append("REASON_NOT_SPECIFIED");
            break;
        case PresentationResult.ABSTRACT_SYNTAX_NOT_SUPPORTED:
            message.append("ABSTRACT_SYNTAX_NOT_SUPPORTED");
            break;
        case PresentationResult.PROPOSED_TRANSFER_SYNTAXES_NOT_SUPPORTED:
            message.append("PROPOSED_TRANSFER_SYNTAXES_NOT_SUPPORTED");
            break;
        case PresentationResult.LOCAL_LIMIT_EXCEEDED:
            message.append("LOCAL_LIMIT_EXCEEDED");
            break;
        default:
            message.append("unknown");
        }
        return message.toString();
    }

}
