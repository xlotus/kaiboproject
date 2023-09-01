package cn.cibn.kaibo.ui.settings;

import android.view.LayoutInflater;

import cn.cibn.kaibo.databinding.FragmentAboutBinding;
import cn.cibn.kaibo.ui.KbBaseFragment;

public class AboutFragment extends KbBaseFragment<FragmentAboutBinding> {


    public static AboutFragment createInstance() {
        return new AboutFragment();
    }

    @Override
    protected FragmentAboutBinding createBinding(LayoutInflater inflater) {
        return FragmentAboutBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        String info = "国广鑫华投资管理（北京）有限公司是第一家经国家广播电视总局许可、在互联网电视平台开展直播电商业务的公司。公司致力于互联网电视电商直播业务“中广直播间”项目，该项目立足于互联网电视大屏的公信力，以及移动端小屏交互的便捷性，满足基于互联网电视相关视频直播业务的应用需求；借助技术平台，将快速通过直播与交互来集合用户、主播、商品等要素，实现各类直播电商视频服务，并快速汇聚行业论坛、行业峰会、政府直播、形象代言等各类视频直播。\n" +
                "\n" +
                "公司以国家广播电视总局批复意见中“两个封闭”为原则，以互联网电视平台为基础，通过引入互联网优秀直播内容资源、人才资源、电商资源，充分利用智能电视大屏的媒体优势对内容、交互、商业模式进行优化和强化。\n" +
                "\n" +
                "中广直播间项目以“CIBN开播”产品作为互联网电视大屏直播的流量入口和直播能力平台，打造业务和技术的总分两级体系架构，统一品牌、统一终端、统一平台、统一播控、分级运营。通过整合并汇聚各类直播内容来突出横屏模式的直播内容呈现；通过大小屏联动，大小屏同步呈现，充分利用大屏的视听优势，大屏做内容展现，小屏做转化，小屏主创作，优质主播和优质内容通过大屏播出，并构建大屏粉丝的互动生态。\n" +
                "\n" +
                "公司将以中广直播间项目为契机，在“大屏＋直播”的媒体内容类业务融合创新上，通过原创性思想和开拓性工作，克服困难，解决问题，积累新做法和新经验，将中广直播间项目发展成为互联网电视大屏生态的新增长点，成为互联网电视行业乃至整个广电行业大屏的旗舰项目，为媒体融合发展事业贡献国广鑫华力量。";
        binding.tvAboutInfo.setText(info);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void updateView() {

    }

    @Override
    public void requestFocus() {

    }

}
