package com.noahedu.fish.param;

public class GameConstant {
	//public static final int SCREEN_WIDTH = 1024;
	
	public static final int U28_DEL_Y = 100;

	// public static final String DEFAULT_DIR = "/mnt/sdcard/";
	
	public static final String DEFAULT_DIR = "/noahdata/";

	/**
	 * flash资源路径
	 */
	public static final String SWF_PATH = DEFAULT_DIR + "fish/swf/";

	/**
	 * 钓鱼开场flash
	 */
	public static final String SWF_START = SWF_PATH + "start.swf";

	/**
	 * 钓鱼过场flash
	 */
	public static final String SWF_MIDDLE = SWF_PATH + "middle.swf";

	/**
	 * 钓鱼片尾flash
	 */
	public static final String SWF_END = SWF_PATH + "end.swf";

	/**
	 * 声音资源路径
	 */
	public static final String SOUND_PATH = DEFAULT_DIR + "fish/mp3/";

	/**
	 * 封面背景音乐
	 */
	public static final String SOUND_yx_79 = SOUND_PATH + "yx_79.mp3";

	/**
	 * 钓鱼界面背景音乐
	 */
	public static final String SOUND_yx_80 = SOUND_PATH + "yx_80.mp3";

	/**
	 * 所有按钮的点击提示音
	 */
	public static final String SOUND_yx_81 = SOUND_PATH + "yx_81.mp3";

	/**
	 * 船移动的音效
	 */
	public static final String SOUND_yx82 = SOUND_PATH + "yx_82.mp3";

	/**
	 * 下钩的音效
	 */
	public static final String SOUND_yx_83 = SOUND_PATH + "yx_83.mp3";

	/**
	 * 鱼钩触碰到物体吸附住的音效
	 */
	public static final String SOUND_yx84 = SOUND_PATH + "yx_84.mp3";

	/**
	 * 收钩转动齿轮的音效
	 */
	public static final String SOUND_yx85 = SOUND_PATH + "yx_85.mp3";

	/**
	 * 调到鱼时候的激励音效
	 */
	public static final String SOUND_yx86 = SOUND_PATH + "yx_86.mp3";

	/**
	 * 钓到杂物时的可惜音效
	 */
	public static final String SOUND_yx87 = SOUND_PATH + "yx_87.mp3";

	/**
	 * 杂物落到船上的音效
	 */
	public static final String SOUND_yx88 = SOUND_PATH + "yx_88.mp3";

	/**
	 * 船沉没的音效
	 */
	public static final String SOUND_yx89 = SOUND_PATH + "yx_89.mp3";

	/**
	 * 一次钓到3条鱼的激励音效
	 */
	public static final String SOUND_yx90 = SOUND_PATH + "yx_90.mp3";

	/**
	 * （封面）小小钓鱼王
	 */
	public static final String SOUND_XB_309 = SOUND_PATH + "XB_309.mp3";

	/**
	 * 点击画面放下鱼钩吧
	 */
	public static final String SOUND_XB_310 = SOUND_PATH + "XB_310.mp3";

	/**
	 * 呃，太重了，猛地点击画面吧
	 */
	public static final String SOUND_XB_311 = SOUND_PATH + "XB_311.mp3";

	/**
	 * 嗷，不是吧
	 */
	public static final String SOUND_XB_312 = SOUND_PATH + "XB_312.mp3";

	/**
	 * 呃，船快要沉了
	 */
	public static final String SOUND_XB_313 = SOUND_PATH + "XB_313.mp3";

	/**
	 * 抓紧时间
	 */
	public static final String SOUND_XB_314 = SOUND_PATH + "XB_314.mp3";

	/**
	 * 我充满力量咯
	 */
	public static final String SOUND_XB_315 = SOUND_PATH + "XB_315.mp3";

	/**
	 * 哎呀，救命啊
	 */
	public static final String SOUND_XB_316 = SOUND_PATH + "XB_316.mp3";

	/**
	 * 准备，开始
	 */
	public static final String SOUND_FISHXB_296 = SOUND_PATH + "FISHXB_296.mp3";

	/**
	 * 太棒了
	 */
	public static final String SOUND_FISHXB_298 = SOUND_PATH + "FISHXB_298.mp3";

	/**
	 * 做的好
	 */
	public static final String SOUND_FISHXB_299 = SOUND_PATH + "FISHXB_299.mp3";

	/**
	 * OH,YEAH!
	 */
	public static final String SOUND_FISHXB_300 = SOUND_PATH + "FISHXB_300.mp3";

	/**
	 * 终于可以休息一下了，一起来看看我们学到哪些生字吧
	 */
	public static final String SOUND_FISHXB_306 = SOUND_PATH + "FISHXB_306.mp3";

	/**
	 * 终于可以休息一下了，一起来看看我们学到哪些字母吧
	 */
	public static final String SOUND_FISHXB_307 = SOUND_PATH + "FISHXB_307.mp3";

	/**
	 * 游戏结束
	 */
	public static final String SOUND_FISHXB_308 = SOUND_PATH + "FISHXB_308.mp3";

	/**
	 * 钓到鱼的激励声音
	 */
	public static final String[] SOUND_RIGHT = { SOUND_FISHXB_298,
			SOUND_FISHXB_299, SOUND_FISHXB_300 };

	/**
	 * 钓到杂物的警告声音
	 */
	public static final String[] SOUND_WRONG = { SOUND_XB_313, SOUND_XB_312,
			SOUND_XB_316 };
}
