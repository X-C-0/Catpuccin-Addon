package me.pindour.catpuccin.gui.animation;

public class Animation {
    private final AnimationType type;
    private final long duration;

    private long startTime;
    private boolean running = false;
    private boolean finished = false;
    private Direction direction;

    public Animation(AnimationType type, long durationMillis) {
        this.type = type;
        this.duration = durationMillis;
        this.direction = Direction.FORWARDS;
    }

    public void start() {
        start(false);
    }

    public void start(boolean backwards) {
        startTime = System.currentTimeMillis();
        running = true;
        finished = false;
        direction = backwards ? Direction.BACKWARDS : Direction.FORWARDS;
    }

    public void setInitialState(Direction dir) {
        running = false;
        finished = true;
        direction = dir;
        startTime = System.currentTimeMillis() - (dir.isForwards() ? duration : 0);
    }

    public void reverse() {
        double currentProgress = getProgress();
        direction = direction.opposite();

        long newElapsed;
        if (direction.isForwards()) newElapsed = (long)(currentProgress * duration);
        else newElapsed = (long)((1.0 - currentProgress) * duration);

        startTime = System.currentTimeMillis() - newElapsed;
        running = true;
        finished = false;
    }

    public void reset() {
        startTime = System.currentTimeMillis();
        running = false;
        finished = false;
        direction = Direction.FORWARDS;
    }

    public double getProgress() {
        boolean forward = direction.isForwards();

        if (!running) {
            if (finished) return forward ? 1.0 : 0.0;
            else return forward ? 0.0 : 1.0;
        }

        long elapsed = System.currentTimeMillis() - startTime;

        if (elapsed >= duration) {
            running = false;
            finished = true;
            return forward ? 1.0 : 0.0;
        }

        double t = (double) elapsed / duration;
        double easedProgress = applyEasing(t);

        return forward ? easedProgress : 1.0 - easedProgress;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isFinished() {
        return finished;
    }

    public Direction getDirection() {
        return direction;
    }

    private double applyEasing(double t) {
        return switch (type) {
            case Linear -> t;
            case EaseIn -> t * t;
            case EaseOut -> 1.0 - Math.pow(1.0 - t, 2);
            case EaseInOut -> t < 0.5
                    ? 2.0 * t * t
                    : 1.0 - 2.0 * Math.pow(1.0 - t, 2);
        };
    }
}
