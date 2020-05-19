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

    //模块化与组件化


    //普通情况下，项目如何构建？
    //没有实施组件化之前，整个项目是一个主工程(com.android.application)，多个子工程moudle(com.android.library)
    //子工程可以是一些网络库，mqtt库等供主工程和其他业务子工程依赖，也可以是一些业务模块（包含页面,模块化的意思？），这样各个业务模块因为页面跳转等原因就可能相互耦合。
    //如果把这些业务模块写在主工程中，就相当于把所有业务都耦合在了主工程中。如果某个业务下线或改动，就需要改动那些跟他耦合在一起的主工程或者其他业务模块。


    //模块化
    //每个模块搞成一个子工程，但各个模块之间可能是相互耦合的。

    //组件化
    //每个组件搞成一个子工程，各个组件之间完全解耦。


    //问题：模块和组件到底谁的粒度大小？
    //业务模块能否包含功能组件？业务组件能否包含功能模块？
    //个人理解：应该从功能和业务两个维度进行区分，无论是模块还是组件，业务肯定会包含单一的功能。


    //------------------------------------------------------------------------------

    //代码解耦的首要目标就是组件之间的完全隔离，在开发过程中我们要时刻牢记，
    // 我们不仅不能直接使用其他组件中的类，最好能根本不了解其中的实现细节。

    //在开发阶段，所有组件中的类我们都是不可以访问的。只有实现了这个目标，
    // 才能从根本上杜绝直接引用组件中类的问题。

    //------------------------------------------------------------------------------


    //https://github.com/wangyongkai/ComponentDemo


    //------------------------------------------------------------------------------

    //问题1：依赖冲突到底什么时候才能发生？


    //app依赖base 组件依赖base  app又依赖组件  会产生重复依赖问题吗
    //不会 重复依赖同一个库 不会发生冲突 如果真有重复依赖的问题，在你编译打包的时候就会报错.
    // 在构建APP的过程中Gradle会自动将重复的arr包排除，APP中也就不会存在相同的代码了；


    //1.app 依赖base不管是implementation还是api  如果base再implementation依赖componentbase
    // 那么app中就不能使用componentbase中的类了  个人理解：该依赖方式所依赖的库不会传递，只会在当前module中生效。虽然不传递，最后打包肯定要打进去。
    //2.api：跟2.x版本的 compile完全相同  该依赖方式会传递所依赖的库，当其他module依赖了该module时，可以使用该module下使用api依赖的库。


    //implementation的作用：1. 加快编译速度。2. 隐藏对外不必要的接口。   A 依赖 B  B implementation C   如果修改C 只需要编译B即可 因为A看不到C中的类啊 没法代码中使用
//问题：为什么会加快编译速度？


    //--------------------------
    //疑惑1：implementation能解决包依赖冲突问题吗？工程implementation依赖了A C， C中implementation又依赖了A的不同版本，会怎么样？
    //工程能使用自己依赖的A的类 但是看不到C中依赖的不同版本A的类 那么打包的时候 如果把工程自己依赖的A打进去  那么C因为依赖不同的版本A可能方法不一样 不会报错吗？？？


    //疑惑2：依赖不同的包可以通过exclude方式排除
    //就是一个库 exclude 库中冲突的库 那么打完包 如果这个库依赖的库跟主工程中的库版本不一样。不就可能不正常运行了吗（版本不同导致方法没有等问题，个人理解此时肯定打包不过，或者新版本向下兼容能打包过）


//举例：第三方包'liji.library.dev:citypickerview:4.1.1'中引用的v7包和项目引用的v7包冲突导致

//报错：java.lang.RuntimeException: com.android.builder.dexing.DexArchiveMergerException:
//Unable to merge dex

//解决：dependencies {
//    implementation 'com.android.support:appcompat-v7:28.0.0'
//    implementation ('liji.library.dev:citypickerview:4.1.1') {
//        exclude group: 'com.android.support'
//    }
//}


    //解惑结论：
    // 1.相同版本库重复依赖不会产生冲突。重复依赖不同版本库才会产生冲突。
    //2.不同版本库的exclude要注意保留最新的。不然不能向下兼容。
    //---------------------------


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
    //注意：不是靠上层的app做为桥梁 而是靠共同依赖的底层库作为桥梁。（这个貌似与依赖倒置原则 相反呵？）


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
    // 设置了resourcePrefix值后，所有的资源名必须以指定的字符串做前缀，否则会报错。
    //但是resourcePrefix这个值只能限定xml里面的资源，并不能限定图片资源，所有图片资源仍然需要手动去修改资源名。


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
