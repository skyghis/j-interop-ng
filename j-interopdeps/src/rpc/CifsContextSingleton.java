package rpc;

import jcifs.CIFSException;
import jcifs.Configuration;
import jcifs.config.BaseConfiguration;
import jcifs.context.BaseContext;

public class CifsContextSingleton extends BaseContext {

    private static final EditableConfiguration CONFIGURATION = EditableConfiguration.newInstance();
    private static CifsContextSingleton CIFS_BASE_CONTEXT = null;

    private CifsContextSingleton(Configuration configuration) {
        super(configuration);
    }

    public static CifsContextSingleton instance() {
        synchronized (CifsContextSingleton.class) {
            if (CIFS_BASE_CONTEXT == null) {
                CIFS_BASE_CONTEXT = new CifsContextSingleton(CONFIGURATION);
            }
            return CIFS_BASE_CONTEXT;
        }
    }

    @Override
    public EditableConfiguration getConfig() {
        return CONFIGURATION;
    }

    public static final class EditableConfiguration extends BaseConfiguration {

        private EditableConfiguration() throws CIFSException {
            super(true);
        }

        private static EditableConfiguration newInstance() {
            try {
                return new EditableConfiguration();
            } catch (CIFSException ex) {
                throw new IllegalStateException("Failed to create default CIFS configuration", ex);
            }
        }

        public void setAllTimeout(int timeoutMillis) {
            smbResponseTimeout = timeoutMillis;
            smbSocketTimeout = timeoutMillis;
            smbConnectionTimeout = timeoutMillis;
            smbSessionTimeout = timeoutMillis;
        }
    }
}
