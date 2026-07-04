package com.dt.animations;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;

public class FadeInTransition extends ConfigAnimation {

    public FadeInTransition(final Node node) {
        super(
                node,
                new Timeline(
                        new KeyFrame(Duration.millis(0),
                                new KeyValue(node.opacityProperty(), 0, WEB_EASE)
                        ),
                        new KeyFrame(Duration.millis(1000),
                                new KeyValue(node.opacityProperty(), 1, WEB_EASE)
                        )
                )
        );

        setCycleDuration(Duration.seconds(2));
        setDelay(Duration.seconds(0.2));
    }
}