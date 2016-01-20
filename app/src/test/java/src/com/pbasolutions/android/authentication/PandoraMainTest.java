package src.com.pbasolutions.android.authentication;

import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import com.pbasolutions.android.PandoraConstant;
import com.pbasolutions.android.PandoraMain;
import com.pbasolutions.android.authentication.PBSServerAuthenticate;
import com.pbasolutions.android.json.PBSLoginJSON;

import org.junit.Test;

/**
 * Created by pbadell on 8/20/15.
 */
public class PandoraMainTest extends ActivityInstrumentationTestCase2<PandoraMain>{

    private PandoraMain pandoraMain;
    public PandoraMainTest() {
        super(PandoraMain.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Starts the activity under test using the default Intent with:
        // action = {@link Intent#ACTION_MAIN}
        // flags = {@link Intent#FLAG_ACTIVITY_NEW_TASK}
        // All other fields are null or empty.
        pandoraMain = getActivity();
        //mFirstTestText = (TextView) mFirstTestActivity.findViewById(R.id.my_first_test_text_view);
    }

    /**
     * Test if your test fixture has been set up correctly. You should always implement a test that
     * checks the correct setup of your test fixture. If this tests fails all other tests are
     * likely to fail as well.
     */
    public void testPreconditions() {
        //Try to add a message to add context to your assertions. These messages will be shown if
        //a tests fails and make it easy to understand why a test failed
        assertNotNull("mFirstTestActivity is null", pandoraMain);
    }


    @Test
    public void testCallServer() {
        PBSLoginJSON pbsLoginJSON = null;
        Bundle resultBundle = new Bundle();
        try {
            resultBundle.putString("testing", "testValue");
        //    PBSServerAuthenticate serverAuthenticate = new PBSServerAuthenticate();
          //  resultBundle = serverAuthenticate.callServer("", pbsLoginJSON, PBSLoginJSON.class.getName().toString(), new Bundle());
        //    pbsLoginJSON = (PBSLoginJSON)resultBundle.getSerializable(PandoraConstant.RESULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
