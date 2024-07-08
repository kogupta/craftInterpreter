package org.kogu.lox.ch6_parser;

public interface ErrorReporter {
    void handle(Error error);
    boolean receivedError();
    void reset();

    static ErrorReporter console() {
        return new ConsoleErrorReporter();
    }
    static ErrorReporter fakeReporter() {
        return new FakeErrorReporter();
    }

    final class ConsoleErrorReporter implements ErrorReporter {

        private boolean receiverError = false;

        @Override
        public void handle(Error error) {
            receiverError = true;
            System.err.println(error);
        }

        @Override
        public boolean receivedError() {
            return receiverError;
        }

        @Override
        public void reset() {
            this.receiverError = false;
        }
    }

    final class FakeErrorReporter implements ErrorReporter {

        private Error error;

        public Error getError() {
            return error;
        }

        @Override
        public void handle(Error error) {
            this.error = error;
        }

        @Override
        public boolean receivedError() {
            return error != null;
        }

        @Override
        public void reset() {
            error = null;
        }
    }
}
