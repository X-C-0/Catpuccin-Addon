package me.pindour.catpuccin.gui.animation;

public class Animation {
    private final Easing easing;
    private final long duration;

    private long startTime;
    private State state = State.IDLE;
    private Direction direction;

    private enum State { IDLE, RUNNING, FINISHED }

    public Animation(Easing easing, long durationMillis) {
        this.easing = easing;
        this.duration = durationMillis;
        this.direction = Direction.FORWARDS;
    }

    public Animation(Easing easing, long durationMillis, Direction initialDirection) {
        this.easing = easing;
        this.duration = durationMillis;
        this.direction = initialDirection;
        finishedAt(initialDirection);
    }

    public void start() {
        start(Direction.FORWARDS);
    }

    public void start(Direction direction) {
        this.startTime = System.currentTimeMillis();
        this.state = State.RUNNING;
        this.direction = direction;
    }

    public void finishedAt(Direction dir) {
        state = State.FINISHED;
        direction = dir;
        startTime = System.currentTimeMillis() - (dir.isForwards() ? duration : 0);
    }

    public void reverse() {
        double progress = getProgress();
        direction = direction.opposite();

        double remainingProgress = direction.isForwards() ?
                (1.0 - progress) : progress;

        long newElapsed = (long)((1.0 - remainingProgress) * duration);
        startTime = System.currentTimeMillis() - newElapsed;

        state = State.RUNNING;
    }

    public void reset() {
        startTime = System.currentTimeMillis();
        state = State.IDLE;
        direction = Direction.FORWARDS;
    }

    public double getProgress() {
        boolean forward = direction.isForwards();

        if (state != State.RUNNING) {
            if (state == State.FINISHED) return forward ? 1.0 : 0.0;
            return forward ? 0.0 : 1.0;
        }

        long elapsed = System.currentTimeMillis() - startTime;

        if (elapsed >= duration) {
            state = State.FINISHED;
            return forward ? 1.0 : 0.0;
        }

        double t = (double) elapsed / duration;
        double easedProgress = easing.apply(t);

        return forward ? easedProgress : 1.0 - easedProgress;
    }

    public boolean isRunning() {
        return state == State.RUNNING;
    }

    public boolean isFinished() {
        return state == State.FINISHED;
    }
}