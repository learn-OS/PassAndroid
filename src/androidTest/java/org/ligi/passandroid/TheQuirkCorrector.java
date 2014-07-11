package org.ligi.passandroid;

import android.test.suitebuilder.annotation.SmallTest;

import org.ligi.passandroid.model.Pass;

import static org.fest.assertions.api.Assertions.assertThat;

public class TheQuirkCorrector extends TheAppleStyleBarcodeReaderBase {

    @SmallTest
    public void testWestbahnDescriptionIsFixed() throws Exception {
        loadPassFromAsset("passes/workarounds/westbahn/special.pkpass", new OnPassLoadCallback() {
            @Override
            public void onPassLoad(Pass pass) {
                assertThat(pass.getDescription()).isEqualTo("Wien Westbahnhof->Amstetten");
            }
        });

    }

}
