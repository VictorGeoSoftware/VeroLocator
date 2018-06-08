package com.mshop.internal.verolocator;

import com.mshop.internal.verolocator.data.MessageCode;
import com.mshop.internal.verolocator.presenter.PositionPresenter;
import com.mshop.internal.verolocator.repository.PositionRepository;
import com.mshop.internal.verolocator.repository.bodies.RefreshLocationBody;
import com.mshop.internal.verolocator.repository.responses.BasicResponseDto;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.Times;

import io.reactivex.schedulers.TestScheduler;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class PositionPresenterTest {
    @Mock PositionPresenter.PositionView positionView;
    private PositionPresenter positionPresenter;
    private TestScheduler testScheduler;



    // ----------------------------------------------------------------------------------------------------------
    // --------------------------------------------- TEST PREPARING ---------------------------------------------
    private PositionPresenter createMockedPresenter() {
        testScheduler = new TestScheduler();
        PositionPresenter positionPresenter = new PositionPresenter(testScheduler, testScheduler);
        positionPresenter.setPositionView(positionView);

        return positionPresenter;
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        positionPresenter = createMockedPresenter();
    }



    // ----------------------------------------------------------------------------------------------------------
    // --------------------------------------------- TEST CASUISTIC ---------------------------------------------
    @Test
    public void shouldCallTo_RefreshLocation_AndRetrieveSomeResponse() {
        RefreshLocationBody body = new RefreshLocationBody(100, "32314", 100, 40.034532, -3.123);

        positionPresenter.callToRefreshLocation(body);
        testScheduler.triggerActions();

        Mockito.verify(positionView, Mockito.times(1)).onPositionUpdated(Mockito.<BasicResponseDto>any());
    }

    @Test
    public void shouldCallTo_RefreshLocation_AndRetrieveServerError() {
        RefreshLocationBody body = new RefreshLocationBody(-1, "32314", 100, 40.034532, -3.123);

        positionPresenter.callToRefreshLocation(body);
        testScheduler.triggerActions();

        Mockito.verify(positionView, Mockito.times(1)).onPositionServerError(Mockito.<MessageCode>any());
    }
}