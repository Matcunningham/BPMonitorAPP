//package cecs343.bpmontor;
package android.os;
/**
 * Created by Mat on 5/4/2018.
 */

/**
 * This is a shadow class for AsyncTask which forces it to run synchronously.
 * Used for testing purposes only.
 */
public abstract class AsyncTask<Params, Progress, Result> {

    protected abstract Result doInBackground(Params... params);

    protected void onPostExecute(Result result) {
    }

    public AsyncTask<Params, Progress, Result> execute(Params... params) {
        Result result = doInBackground(params);
        onPostExecute(result);
        return this;
    }

}
