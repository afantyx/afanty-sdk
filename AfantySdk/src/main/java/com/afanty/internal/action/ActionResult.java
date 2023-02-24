package com.afanty.internal.action;

public class ActionResult {
    public boolean mActionResult;
    public boolean mIsDownloadAction;
    public boolean mIsOfflineAction;
    public boolean mSupportActionReport;

    public ActionResult(Builder builder) {
        mActionResult = builder.actionResult;
        mIsOfflineAction = builder.offlineAction;
        mSupportActionReport = builder.supportActionReport;
    }

    public static class Builder {
        private final boolean actionResult;
        private boolean offlineAction;
        private boolean supportActionReport = true;

        public Builder(boolean actionResult) {
            this.actionResult = actionResult;
        }

        public Builder offlineAction(boolean offlineAction) {
            this.offlineAction = offlineAction;
            return this;
        }

        public Builder supportActionReport(boolean supportActionReport) {
            this.supportActionReport = supportActionReport;
            return this;
        }

        public ActionResult build() {
            return new ActionResult(this);
        }

    }

}
