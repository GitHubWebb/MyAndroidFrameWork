package com.actor.myandroidframework.adapter_recyclerview;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntRange;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.actor.myandroidframework.R;
import com.actor.myandroidframework.utils.picture_selector.PictureSelectorUtils;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * description: 添加音频, 目前只有选择音频功能, 使用示例: {@link AddLocalMediaAble}
 *
 * @author : 李大发
 * date       : 2020/9/18 on 20:28
 * @version 1.0
 */
public class AddAudioAdapter<UploadInfo> extends BaseQuickAdapter<LocalMedia, BaseViewHolder> implements AddLocalMediaAble<UploadInfo> {

    public static final int TYPE_RECORD_AUDIO = 0;//录制音频
    public static final int TYPE_SELECT_AUDIO = 1;//选择音频
    public static final int TYPE_RECORD_SELECT_AUDIO = 2;//录制音频&选择音频

    private       int              maxFiles;//最多选择多少个
    private final int              selectType;//选择类型
    @DrawableRes
    private final int              lastItemPic;//最后一个Item显示的图片
    @DrawableRes
    private final int              normalItemPic;//已选择Item显示的图片
    private final List<LocalMedia> localMedias = new ArrayList<>();

    public AddAudioAdapter(int maxFile, @IntRange(from = TYPE_RECORD_AUDIO, to = TYPE_RECORD_SELECT_AUDIO) int type) {
        this(maxFile, type, LAYOUT_RES_ID, R.drawable.audio_gray_for_file_select, R.drawable.headset_gray_for_file_select);
    }

    /**
     * @param maxFile 最多选择多少个音频
     * @param type 选择类型
     * @param layoutResId 自定义Item布局
     * @param lastItemPic 最后一个Item显示的图片
     */
    public AddAudioAdapter(int maxFile, @IntRange(from = TYPE_RECORD_AUDIO, to = TYPE_RECORD_SELECT_AUDIO) int type,
                           @LayoutRes int layoutResId, @DrawableRes int lastItemPic, @DrawableRes int normalItemPic) {
        super(layoutResId);
        this.maxFiles = maxFile;
        this.selectType = type;
        this.lastItemPic = lastItemPic;
        this.normalItemPic = normalItemPic;
        initAddLocalMediaAble();
        addData(EXTRA_LAST_MEDIA);//添加一个+号

        setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                //是否是最后一个pos
                boolean isLastPos = position == getItemCount() - 1;
                int id = view.getId();
                if (id == R.id.iv_for_file_select) {//添加
                    if (isLastPos) {
                        //判断是否能选择更多
                        if (getItemCount() > maxFiles) {
                            ToastUtils.showShort("最多选择%d个", maxFiles);
                        } else {
                            Activity topActivity = ActivityUtils.getTopActivity();
                            if (topActivity == null) {
                                return;
                            }
                            switch (selectType) {
                                case TYPE_RECORD_AUDIO://录制音频
                                    //调用系统录音
//                                        PictureSelectorUtils.recoreAudio(mContext, null, null);
//                                        break;
                                case TYPE_SELECT_AUDIO://选择音频
                                    PictureSelectorUtils.selectAudio(topActivity, maxFiles, localMedias, new OnResultCallbackListener<LocalMedia>() {
                                        @Override
                                        public void onResult(List<LocalMedia> result) {
                                            localMedias.clear();
                                            localMedias.addAll(result);
                                            result.add(EXTRA_LAST_MEDIA);
                                            setNewData(result);
                                        }

                                        @Override
                                        public void onCancel() {
                                        }
                                    });
                                    break;
                                case TYPE_RECORD_SELECT_AUDIO://录制音频&选择音频
                                    PictureSelectorUtils.selectAudio(topActivity, maxFiles, localMedias, new OnResultCallbackListener<LocalMedia>() {
                                        @Override
                                        public void onResult(List<LocalMedia> result) {
                                            localMedias.clear();
                                            localMedias.addAll(result);
                                            result.add(EXTRA_LAST_MEDIA);
                                            setNewData(result);
                                        }

                                        @Override
                                        public void onCancel() {
                                        }
                                    });
                                    break;
                                default:
                                    break;
                            }

                        }
                    } else {//预览
                        Activity topActivity = ActivityUtils.getTopActivity();
                        if (topActivity != null) {
                            LocalMedia item = getItem(position);
                            if (item != null) {
                                PictureSelectorUtils.previewAudio(topActivity, item.getRealPath());
                            }
                        }
                    }
                } else if (id == R.id.iv_delete_for_file_select) {//删除
                    remove(position);
                    localMedias.remove(position);
                }
            }
        });
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, LocalMedia item) {
        //是否是最后一个pos
        boolean isLastPos = helper.getAdapterPosition() == getItemCount() - 1;
        ImageView iv = helper.setGone(R.id.iv_delete_for_file_select, !isLastPos)
                .addOnClickListener(R.id.iv_for_file_select, R.id.iv_delete_for_file_select)
                .getView(R.id.iv_for_file_select);
        if (isLastPos) {
            Glide.with(iv).load(lastItemPic).into(iv);
        } else {
            Glide.with(iv).load(normalItemPic).into(iv);
        }
    }

    /**
     * @param maxFile 设置最多选择多少个文件
     */
    public void setMaxFiles(int maxFile) {
        this.maxFiles = maxFile;
    }

    /**
     * 获取已选择的文件
     */
    @Override
    public List<LocalMedia> getSelectFiles() {
        return localMedias;
    }

    /**
     * @deprecated 用户不需要调用这个方法, 应该调用{@link #getSelectFiles() }
     */
    @Deprecated
    @NonNull
    @Override
    public List<LocalMedia> getData() {
        return super.getData();
    }

    private Map<String, UploadInfo> uploads = new LinkedHashMap<>();
    /**
     * @deprecated 用户不需要调用这个方法
     */
    @Deprecated
    @Override
    public Map<String, UploadInfo> getUploads() {
        return uploads;
    }
}