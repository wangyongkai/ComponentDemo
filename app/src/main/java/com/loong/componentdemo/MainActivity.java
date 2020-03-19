package com.loong.componentdemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;

public class MainActivity extends AppCompatActivity {


    //------------------------------------------------------------------------------

    //代码解耦的首要目标就是组件之间的完全隔离，在开发过程中我们要时刻牢记，
    // 我们不仅不能直接使用其他组件中的类，最好能根本不了解其中的实现细节。

    //在开发阶段，所有组件中的类我们都是不可以访问的。只有实现了这个目标，
    // 才能从根本上杜绝直接引用组件中类的问题。

    //------------------------------------------------------------------------------


    //https://github.com/wangyongkai/ComponentDemo


    //------------------------------------------------------------------------------

    //问题1：依赖冲突到底什么时候才能发生？

    //重复依赖同一个库 不会发生冲突

    //app依赖base 组件依赖base  app又依赖组件  会产生重复依赖问题吗
    //如果真有重复依赖的问题，在你编译打包的时候就会报错.
    // 在构建APP的过程中Gradle会自动将重复的arr包排除，APP中也就不会存在相同的代码了；


    //1.app 依赖base不管是implementation还是api  如果base再implementation依赖componentbase
    // 那么app中就不能使用componentbase中的类了  个人理解：该依赖方式所依赖的库不会传递，只会在当前module中生效。
    //2.api：跟2.x版本的 compile完全相同  该依赖方式会传递所依赖的库，当其他module依赖了该module时，可以使用该module下使用api依赖的库。


    //------------------------------------------------------------------------------


    //问题2：BuildConfig问题
    //使用命令打debug包./gradlew :app:assembleDebug 发现library是用release模式构建
    //原因：library的构建永远是使用release模式构建

    //疑问1：修改Build Variants 中library的构建方式呢？ 个人理解肯定管用。
    //疑问2：用新的studio和gradle5.6.4发现并没有上述问题。library是用debug模式构建的。


    //------------------------------------------------------------------------------


    //问题3：组件与组件之间的通信
    //组件与组件相互不依赖。类不同不能相互引用。
    //组件都依赖基础库。可以把基础库作为中间桥梁(接口)。将接口实现注册给接口，然后其他组件通过调用
    // 接口来实际调用被调用组件的接口实现。
    //注意：不是靠上层的app做为桥梁 而是靠共同依赖的底层库作为桥梁。


    //------------------------------------------------------------------------------


    //问题4：组件怎么单独进行调试
    //1.属性配置com.android.application还是library
    //2.如果是application进行单独调试需要配置启动页，而library则不需要。所以，AndroidManifest
    // 也要配置两份。


    //------------------------------------------------------------------------------


    //问题5: app只初始化主app的application 组件中即使定义也不会初始化 所以  放进组件application中的需要初始化的代码该怎么触发？
    //搞一个字符串集合，里面盛放需要初始化的组件的application 然后利用反射逐个进行构造并调用初始化方法


    //------------------------------------------------------------------------------


    //问题6：组件与组件之间不依赖相互解耦 不能相互引用类  activity跳转怎么实现
    //ARouter
    //个人理解：虽然编译阶段组件之间不能相互引用类。但是运行时在dvm中，应该是可以触达的。
    // startactivity(x,,,a.class)  a.class应该在dvm中可达。


    //------------------------------------------------------------------------------

    //问题7：组件对外提供fragment
    //还是依靠底层的接口桥梁

    //个人理解：解耦是什么？ 就是之间不强引用？


    //------------------------------------------------------------------------------

    //问题8：如何避免各个组件之间资源重名
    //resourcePrefix


    //------------------------------------------------------------------------------


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        BuildConfig
    }

    /**
     * 跳登录界面
     *
     * @param view
     */
    public void login(View view) {
        ARouter.getInstance().build("/account/login").navigation();
    }

    /**
     * 跳分享界面
     *
     * @param view
     */
    public void share(View view) {
        ARouter.getInstance().build("/share/share").withString("share_content", "分享数据到微博").navigation();
    }

    /**
     * 跳 FragmentActivity
     *
     * @param view
     */
    public void fragment(View view) {
        startActivity(new Intent(this, FragmentActivity.class));
    }
}
