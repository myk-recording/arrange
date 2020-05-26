package com.readboy.newcurriculum.Utils;


import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.readboy.newcurriculum.R;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SkinFactory implements LayoutInflater.Factory2 {
    private AppCompatDelegate mDelegate;//预定义一个委托类，它负责按照系统的原有逻辑来创建view

    private List<SkinView> listCacheSkinView = new ArrayList<>();

    /**
     * 给外部提供一个set方法
     *
     * @param mDelegate
     */
    public void setDelegate(AppCompatDelegate mDelegate) {
        this.mDelegate = mDelegate;
    }


    /**
     * Factory2 继承Factory，重写Factory的onCreateView逻辑，不理会Factory的重写方法了
     *
     * @param name
     * @param context
     * @param attrs
     * @return
     */
    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return null;
    }

    /**
     * @param parent
     * @param name
     * @param context
     * @param attrs
     * @return
     */
    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {

        View view = mDelegate.createView(parent, name, context, attrs);
        if (view == null) {
            mConstructorArgs[0] = context;
            try {
                if (-1 == name.indexOf('.')) { //不带包名
                    view = createViewByPrefix(context, name, prefixs, attrs);
                } else {//包是权限定名的view name，
                    view = createViewByPrefix(context, name, null, attrs);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        collectSkinView(context, attrs, view);

        return view;
    }

    /**
     * 收集需要换肤的控件
     * 收集的方式是：通过自定义属性，从创建出来的很多View中，找到支持换肤的那些，保存到map中
     */
    private void collectSkinView(Context context, AttributeSet attrs, View view) {
        // 获取自定义的属性
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Skinable);
        boolean isSupport = a.getBoolean(R.styleable.Skinable_supportFlag, false);
        if (isSupport) {//找到支持换肤的view
            final int Len = attrs.getAttributeCount();
            HashMap<String, String> attrMap = new HashMap<>();
            for (int i = 0; i < Len; i++) {//遍历所有属性
                String attrName = attrs.getAttributeName(i);
                String attrValue = attrs.getAttributeValue(i);
                attrMap.put(attrName, attrValue);//全部存起来
            }

            SkinView skinView = new SkinView();
            skinView.view = view;
            skinView.attrsMap = attrMap;
            listCacheSkinView.add(skinView);//将可换肤的view，放到listCacheSkinView中
        }

    }

    /**
     * 公开给外界的换肤入口
     */
    public void changeSkin() {
        for (SkinView skinView : listCacheSkinView) {
            skinView.changeSkin();
        }
    }

    static class SkinView {
        View view;
        HashMap<String, String> attrsMap;

        /**
         * 真正的换肤操作
         */
        public void changeSkin() {
            if (!TextUtils.isEmpty(attrsMap.get("background"))) {//属性名,例如:background，text，textColor....
                int bgId = Integer.parseInt(attrsMap.get("background").substring(1));//属性值，R.id.XXX ，int类型，
                // 这个值，在app的一次运行中，不会发生变化
                String attrType = view.getResources().getResourceTypeName(bgId); // 属性类别：比如 drawable ,color
                if (TextUtils.equals(attrType, "drawable")) {//区分drawable和color
                    view.setBackgroundDrawable(SkinEngine.getInstance().getDrawable(bgId));//加载外部资源管理器，拿到外部资源的drawable
                } else if (TextUtils.equals(attrType, "color")) {
                    view.setBackgroundColor(SkinEngine.getInstance().getColor(bgId));
                }
            }

            if (view instanceof TextView) {
                if (!TextUtils.isEmpty(attrsMap.get("textColor"))) {
                    int textColorId = Integer.parseInt(attrsMap.get("textColor").substring(1));
                    ((TextView) view).setTextColor(SkinEngine.getInstance().getColor(textColorId));
                }
            }
        }

    }


    // AppCompatViewInflater中view = createViewFromTag(context, name, attrs);
    static final Class<?>[] mConstructorSignature = new Class[]{Context.class, AttributeSet.class};//
    final Object[] mConstructorArgs = new Object[2];//View的构造函数的2个"实"参对象
    private static final HashMap<String, Constructor<? extends View>> sConstructorMap = new HashMap<String, Constructor<? extends View>>();//用映射，将View的反射构造函数都存起来
    static final String[] prefixs = new String[]{//安卓里面控件的包名，为反射创建类的class而预备的
            "android.widget.",
            "android.view.",
            "android.webkit."
    };

    /**
     * 反射创建View
     *
     * @param context
     * @param name 包名
     * @param prefixs 安卓里面控件的包名
     * @param attrs
     * @return
     */
    private final View createViewByPrefix(Context context, String name, String[] prefixs, AttributeSet attrs) {

        Constructor<? extends View> constructor = sConstructorMap.get(name);
        Class<? extends View> clazz = null;

        if (constructor == null) {
            try {
                if (prefixs != null && prefixs.length > 0) {
                    for (String prefix : prefixs) {
                        /**
                         * asSubclass()将一个Class对象转换成指定的泛型的对象
                         */
                        clazz = context.getClassLoader().loadClass(
                                prefix != null ? (prefix + name) : name).asSubclass(View.class);//控件

                        if (clazz != null) break;
                    }
                } else {
                    if (clazz == null) {
                        clazz = context.getClassLoader().loadClass(name).asSubclass(View.class);
                    }
                }
                if (clazz == null) {
                    return null;
                }
                constructor = clazz.getConstructor(mConstructorSignature);//拿到 构造方法，
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            constructor.setAccessible(true);//允许对类的私有字段进行操作
            sConstructorMap.put(name, constructor);//然后缓存起来，下次再用，就直接从内存中去取
        }
        Object[] args = mConstructorArgs;
        args[1] = attrs;
        try {
            //通过反射创建View对象
            final View view = constructor.newInstance(args);//执行构造函数，拿到View对象
            return view;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
