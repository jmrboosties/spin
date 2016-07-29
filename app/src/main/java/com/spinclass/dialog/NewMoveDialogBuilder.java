package com.spinclass.dialog;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import com.spinclass.R;
import com.spinclass.base.BaseActivity;

public class NewMoveDialogBuilder {

	private BaseActivity mActivity;
	private String mCurrentTimestamp;
	private OnConfirmClickListener mListener;

	public NewMoveDialogBuilder(BaseActivity activity, String currentTimestamp, @NonNull OnConfirmClickListener listener) {
		mActivity = activity;
		mCurrentTimestamp = currentTimestamp;
		mListener = listener;
	}

	public AlertDialog build() {
		//Build the layout first
		@SuppressLint("InflateParams")
		View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_new_move, null);

		final EditText time = (EditText) view.findViewById(R.id.dnm_time);
		final EditText description = (EditText) view.findViewById(R.id.dnm_edittext);

		time.setText(mCurrentTimestamp);

		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

		builder.setTitle(R.string.new_move);
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mListener.onConfirmClicked(time.getText().toString(), description.getText().toString());
			}

		});

		AlertDialog dialog = builder.create();
		dialog.setView(view);

		return dialog;
	}

	public interface OnConfirmClickListener {

		void onConfirmClicked(String time, String description);

	}

}
