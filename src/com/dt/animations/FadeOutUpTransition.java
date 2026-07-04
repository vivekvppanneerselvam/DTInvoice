package com.dt.animations;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;

public class FadeOutUpTransition extends ConfigAnimation {

    public FadeOutUpTransition(final Node node) {
        super(
                node,
                new Timeline(
                        new KeyFrame(Duration.millis(50),
                                new KeyValue(node.opacityProperty(), 1, WEB_EASE),
                                new KeyValue(node.translateYProperty(), 0, WEB_EASE)
                        ),
                        new KeyFrame(Duration.millis(500),
                                new KeyValue(node.opacityProperty(), 0, WEB_EASE),
                                new KeyValue(node.translateYProperty(), -10, WEB_EASE)
                        )
                )
        );

        setCycleDuration(Duration.seconds(1));
        setDelay(Duration.seconds(0));
    }

    @Override
    protected void stopping() {
        super.stopping();
        node.setTranslateY(0);
        node.toBack(); // restore default
    }
}