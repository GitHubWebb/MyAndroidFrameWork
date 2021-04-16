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
 * description: 添加视频, 使用示例: {@link AddLocalMediaAble}
 *
 * @author : 李大发
 * date       : 2020/9/18 on 20:28
 * @version 1.0
 */
public class AddVideoAdapter<UploadInfo> extends BaseQuickAdapter<LocalMedia, BaseViewHolder> implements AddLocalMediaAble<UploadInfo> {

    public static final int TYPE_TAKE_VIDEO = 0;//拍视频
    public static final int TYPE_SELECT_VIDEO = 1;//选择视频
    public static final int TYPE_TAKE_SELECT_VIDEO = 2;//拍视频&选择视频

    private       int              maxFiles;//最多选择多少个
    private final int              selectType;//选择类型
    @DrawableRes
    private final int              lastItemPic;//最后一个Item显示的图片
    private final List<LocalMedia> localMedias = new ArrayList<>();

    public AddVideoAdapter(int maxFile, @IntRange(from = TYPE_TAKE_VIDEO, to = TYPE_TAKE_SELECT_VIDEO) int type) {
        this(maxFile, type, LAYOUT_RES_ID, R.drawable.video_gray_for_file_select);
    }

    /**
     * @param maxFile 最多选择多少个视频
     * @param type 选择类型
     * @param layoutResId 自定义Item布局
     * @param lastItemPic 最后一个Item显示的图片
     */
    public AddVideoAdapter(int maxFile, @IntRange(from = TYPE_TAKE_VIDEO, to = TYPE_TAKE_SELECT_VIDEO) int type,
                           @LayoutRes int layoutResId, @DrawableRes int lastItemPic) {
        super(layoutResId);
        this.maxFiles = maxFile;
        this.selectType = type;
        this.lastItemPic = lastItemPic;
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
                                case TYPE_TAKE_VIDEO://拍视频
                                    PictureSelectorUtils.recordVideo(topActivity, new OnResultCallbackListener<LocalMedia>() {
                                        @Override
                                        public void onResult(List<LocalMedia> result) {
                                            LocalMedia localMedia = result.get(0);
                                            localMedias.add(localMedia);
                                            addData(getData().size() - 1, localMedia);
                                        }

                                        @Override
                                        public void onCancel() {
                                        }
                                    });
                                    break;
                                case TYPE_SELECT_VIDEO://选择视频
                                    PictureSelectorUtils.selectVideos(topActivity, false, maxFiles, localMedias, new OnResultCallbackListener<LocalMedia>() {
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
                                case TYPE_TAKE_SELECT_VIDEO://拍视频&选择视频
                                    PictureSelectorUtils.selectVideos(topActivity, true, maxFiles, localMedias, new OnResultCallbackListener<LocalMedia>() {
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
                                PictureSelectorUtils.previewVideo(topActivity, item.getPath());
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
            Glide.with(iv).load(item.getPath()).into(iv);
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