package io.merklex.settlement.dcn;

import io.merklex.settlement.contracts.DCN;
import org.junit.After;
import org.junit.Before;

public abstract class TestBase {
    protected DCN dcn = StaticNetwork.DCN();

    @Before
    public void setup() {
        StaticNetwork.Checkpoint();
    }

    @After
    public void teardown() {
        StaticNetwork.Revert();
    }
}
