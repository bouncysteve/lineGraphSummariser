package uk.co.lgs.test;

import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

}
