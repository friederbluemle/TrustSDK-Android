package trust;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

public abstract class Trust {

    public static final String ACTION_SIGN_TRANSACTION = "sign-transaction";
    public static final String ACTION_SIGN_MESSAGE = "sign-message";
    public static final String ACTION_SIGN_PERSONAL_MESSAGE = "sign-personal-message";

    public static final int RESULT_ERROR = 1;

    public static final int REQUEST_CODE_SIGN = 8001;

    private static String packageName = "com.wallet.crypto.trustapp";

    private Trust() {}

    public static void setWalletAppPackageName(String packageName) {
        Trust.packageName = packageName;
    }

    public static SignTransactionRequest.Builder signTransaction() {
        return SignTransactionRequest.builder();
    }

    public static SignMessageRequest.Builder signMessage() {
        return SignMessageRequest.builder();
    }

    public static SignMessageRequest.Builder signPersonalMessage() {
        return SignMessageRequest.builder().isPersonal(true);
    }

    public static <T extends Request> Call<T> execute(final Activity activity, T request) {
        final Intent intent = new Intent();
        intent.setData(request.key());
        if (canStartActivity(activity, intent)) {
            activity.startActivityForResult(intent, REQUEST_CODE_SIGN);
            return new Call<>(request);
        } else {
            try {
                activity.startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + packageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                activity.startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
            }
            return null;
        }
    }

    static boolean canStartActivity(Context context, Intent intent) {
        PackageManager pm = context.getPackageManager();
        return pm.queryIntentActivities(intent, 0).size() > 0;
    }

    interface ExtraKey {
        String SIGN = "sign";
        String ERROR = "error";
        String RECIPIENT = "to";
        String VALUE = "amount";
        String GAS_PRICE = "gasPrice";
        String GAS_LIMIT = "gasLimit";
        String CONTRACT = "contract";
        String PAYLOAD = "data";
        String NONCE = "nonce";
        String LEAF_POSITION = "leaf_position";
        String MESSAGE = "message";
    }

    public interface ErrorCode {
        int NONE = -1;
        int CANCELED = 0;
        int SIGN_NOT_AVAILABLE = 1;
        int SIGN_ERROR = 2;
        int WATCH_ONLY = 3;

        int UNKNOWN_ERROR = Integer.MAX_VALUE;
    }
}
