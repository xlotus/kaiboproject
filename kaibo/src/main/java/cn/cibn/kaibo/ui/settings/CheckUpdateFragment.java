package cn.cibn.kaibo.ui.settings;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.core.view.GravityCompat;

import com.bumptech.glide.Glide;
import com.tv.lib.core.Logger;
import com.tv.lib.core.lang.ObjectStore;
import com.tv.lib.core.lang.StringUtils;
import com.tv.lib.core.lang.thread.TaskHelper;
import com.tv.lib.core.utils.Utils;
import com.tv.lib.core.utils.ui.SafeToast;

import java.io.File;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.data.ConfigModel;
import cn.cibn.kaibo.databinding.FragmentCheckUpdateBinding;
import cn.cibn.kaibo.model.ModelVersion;
import cn.cibn.kaibo.model.ModelWrapper;
import cn.cibn.kaibo.network.SettingMethod;
import cn.cibn.kaibo.ui.KbBaseFragment;
import cn.cibn.kaibo.utils.DateUtils;

public class CheckUpdateFragment extends KbBaseFragment<FragmentCheckUpdateBinding> implements View.OnClickListener {
    private static final String TAG = "CheckUpdate";
    private VersionHistoryFragment versionHistoryFragment;

    private ModelVersion.Item versionData;

    public static CheckUpdateFragment createInstance() {
        return new CheckUpdateFragment();
    }

    @Override
    protected FragmentCheckUpdateBinding createBinding(LayoutInflater inflater) {
        return FragmentCheckUpdateBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        binding.tvAppVersion.setText(Utils.getVersionName(ObjectStore.getContext()));
        binding.btnUpdateNow.setVisibility(View.GONE);
        binding.btnUpdateNow.setOnClickListener(this);
        binding.btnHistoryVersions.setOnClickListener(this);
        binding.btnClearCache.setOnClickListener(this);
        binding.drawerVersionHistory.setScrimColor(Color.TRANSPARENT);

        versionHistoryFragment = VersionHistoryFragment.createInstance();
        getChildFragmentManager().beginTransaction().replace(R.id.version_history_container, versionHistoryFragment).commit();
        updateView();
    }

    @Override
    protected void initData() {
        reqCheckVersion();
    }

    @Override
    protected void updateView() {
        if (binding == null) {
            return;
        }
        if (ConfigModel.getInstance().isGrayMode()) {
            binding.ivLauncher.setImageResource(R.drawable.ic_launcher_210_gray);
            binding.btnUpdateNow.setBackgroundResource(R.drawable.shape_22_33ffffff_selector_gray);
            binding.btnHistoryVersions.setBackgroundResource(R.drawable.shape_22_33ffffff_selector_gray);
            binding.btnClearCache.setBackgroundResource(R.drawable.shape_22_33ffffff_selector_gray);
        } else {
            binding.ivLauncher.setImageResource(R.drawable.ic_launcher_210);
            binding.btnUpdateNow.setBackgroundResource(R.drawable.shape_22_33ffffff_selector);
            binding.btnHistoryVersions.setBackgroundResource(R.drawable.shape_22_33ffffff_selector);
            binding.btnClearCache.setBackgroundResource(R.drawable.shape_22_33ffffff_selector);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == binding.btnUpdateNow.getId()) {
            if (versionData != null && !TextUtils.isEmpty(versionData.getFile())) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(versionData.getFile()));
                mContext.startActivity(intent);
            }
        } else if (id == binding.btnHistoryVersions.getId()) {
            binding.drawerVersionHistory.openDrawer(GravityCompat.END);
            versionHistoryFragment.requestFocus();
        } else if (id == binding.btnClearCache.getId()) {
            clearCache();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (binding.drawerVersionHistory.isDrawerOpen(GravityCompat.END)) {
            if (versionHistoryFragment.onKeyDown(keyCode, event)) {
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_BACK) {
                binding.drawerVersionHistory.closeDrawer(GravityCompat.END);
                binding.btnHistoryVersions.requestFocus();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void reqCheckVersion() {
        TaskHelper.exec(new TaskHelper.Task() {
            ModelWrapper<ModelVersion.Item> model;
            @Override
            public void execute() throws Exception {
                model = SettingMethod.getInstance().reqCheckVersion();
            }

            @Override
            public void callback(Exception e) {
                if (model != null && model.isSuccess() && model.getData() != null) {
                    versionData = model.getData();
                    StringBuilder buf = new StringBuilder();
                    buf.append("最新版本：").append(versionData.getVersionNumber());
                    buf.append("    大小：").append(versionData.getFileSize());
                    buf.append("    更新时间：").append(DateUtils.formatDate(versionData.getUpdateTime() * 1000));
                    buf.append("    开发者：CIBN");
                    binding.tvLatestVersion.setText(buf.toString());
                    binding.tvUpdateDetail.setText(Html.fromHtml(versionData.getContent()));
                    if (isVersionNewer(versionData.getVersionNumber())) {
                        binding.btnUpdateNow.setVisibility(View.VISIBLE);
                    } else {
                        binding.btnUpdateNow.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private boolean isVersionNewer(String version) {
        if (TextUtils.isEmpty(version)) {
            return false;
        }
        String curVersion = Utils.getVersionName(ObjectStore.getContext());
        if (TextUtils.isEmpty(curVersion)) {
            return false;
        }
        String[] versionBuf = version.split("\\.");
        String[] curVersionBuf = curVersion.split("\\.");
        for (int i = 0; i < versionBuf.length && i < curVersionBuf.length; i++) {
            if (StringUtils.quietParseToInt(versionBuf[i],0) > StringUtils.quietParseToInt(curVersionBuf[i], 0)) {
                return true;
            }
        }
        return false;
    }

    private void clearCache() {
        TaskHelper.exec(new TaskHelper.Task() {
            @Override
            public void execute() throws Exception {
                File file = Glide.getPhotoCacheDir(ObjectStore.getContext());
                if (file != null) {
                    Logger.d(TAG, "cache size = " + file.getTotalSpace());
                }
                if (mContext != null) {
                    Glide.get(mContext).clearDiskCache();
                }
            }

            @Override
            public void callback(Exception e) {
                SafeToast.showToast("已清理", Toast.LENGTH_SHORT);
            }
        });
    }
}
