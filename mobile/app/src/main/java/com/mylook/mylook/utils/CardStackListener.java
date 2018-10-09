package com.mylook.mylook.utils;

import com.wenchao.cardstack.CardStack;

public class CardStackListener implements CardStack.CardEventListener {

    @Override
    public boolean swipeEnd(int i, float v) {
        return false;
    }

    @Override
    public boolean swipeStart(int i, float v) {
        return false;
    }

    @Override
    public boolean swipeContinue(int i, float v, float v1) {
        return false;
    }

    @Override
    public void discarded(int i, int i1) {

    }

    @Override
    public void topCardTapped() {

    }
}
