package com.car.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.car.entity.TbCameraGunEntity;
import com.car.entity.TbCarEntity;
import com.car.entity.TbChannelEntity;
import com.car.entity.dto.SpeedDTO;
import com.car.entity.vo.SpeedVO;
import com.car.mapper.TbCarMapper;
import com.car.service.TbCameraGunService;
import com.car.service.TbCarService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.car.service.TbChannelService;
import com.car.util.RStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TbCarServiceImpl extends ServiceImpl<TbCarMapper, TbCarEntity> implements TbCarService {
    @Autowired
    private TbCarMapper tbCarMapper;
    @Autowired
    private TbCameraGunService tbCameraGunService;
    @Autowired
    private TbChannelService tbChannelService;
    @Autowired
    private TbCarService tbCarService;

    /**
     * 测速数据
     *
     * @return
     */
    @Override
    public RStatic channelAllData(SpeedDTO speedDTO) {
        try {
            TbChannelEntity channel = new TbChannelEntity();
            if (speedDTO.getChannelId() != null) {
                QueryWrapper<TbChannelEntity> wrapper = new QueryWrapper<>();
                wrapper.eq("id", speedDTO.getChannelId());
                channel = tbChannelService.getOne(wrapper);
            }
            List<SpeedVO> cameraGunListCars = new ArrayList<>();
            List<SpeedVO> listCar = new ArrayList<>();
            SpeedVO carVo = new SpeedVO();
            Map<String, Integer> stringIntegerMap = new HashMap<>();


            if (speedDTO.getStartTime()!=null && speedDTO.getEndTime() != null) {

                cameraGunListCars = tbCarMapper.getMoreDayCars(speedDTO);
                System.out.println("===="+cameraGunListCars.get(0));
                stringIntegerMap = tbCarMapper.moreDayRatio(speedDTO);

            } else {
                cameraGunListCars = tbCarMapper.getOneDayCars(speedDTO);
                System.out.println("==s"+cameraGunListCars.get(0));
                stringIntegerMap = tbCarMapper.oneDayRatio(speedDTO);
            }


            List<SpeedVO> carPage = cameraGunListCars.stream().skip((speedDTO.getPage() - 1) * speedDTO.getItems()).limit(speedDTO.getItems()).collect(Collectors.toList());

            if (speedDTO.getChannelId() != null) {

                return RStatic.ok("查询成功").data("channelName", channel.getName()).data("carPage", carPage).data("dayRatio", stringIntegerMap).data("page", speedDTO.getPage()).data("items", speedDTO.getItems());
            } else {
                return RStatic.ok("查询成功").data("carPage", carPage).data("dayRatio", stringIntegerMap).data("page", speedDTO.getPage()).data("items", speedDTO.getItems());
            }
        } catch (Exception e) {
            System.out.println("错误原因：" + e.getMessage());
        }
        return RStatic.error("错误");
    }

    @Override
    public RStatic channelOneCarData(SpeedDTO speedDTO) {
        Map<Integer, Map<String, Integer>> map = new HashMap<>();

        Date hour = new Date();
        SimpleDateFormat df = new SimpleDateFormat("HH");
        Integer hourInt;
        if (speedDTO.getStartTime() != null && speedDTO.getChannelId() != null) {

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String startTimeStr = formatter.format(speedDTO.getStartTime());
            long isTime = getDaySub(startTimeStr, formatter.format(new Date()));
            System.out.println("====isTime" + isTime);
            if (isTime < 1) {
                hourInt = Integer.parseInt(df.format(hour));
                for (int i = 0; i < hourInt; i++) {
                    Map<String, Integer> hourMap = tbCarMapper.hourRatio(speedDTO, i + 1);
                    map.put(i + 1, hourMap);

                }
            }
        }

        List<SpeedVO> cameraGunListCars = tbCarMapper.getOneDayCars(speedDTO);
        SpeedVO car = cameraGunListCars.get(0);

        if (speedDTO.getStartTime() != null && speedDTO.getChannelId() != null) {
            Map<String, Integer> stringIntegerMap = tbCarMapper.oneDayRatio(speedDTO);
            return RStatic.ok("查询成功").data("car", car).data("hourRatio", map).data("dayRatio", stringIntegerMap).data("page", speedDTO.getPage()).data("items", speedDTO.getItems());

        } else {
            return RStatic.ok("查询成功").data("car", car).data("hourRatio", map).data("page", speedDTO.getPage()).data("items", speedDTO.getItems());

        }
    }

    public static long getDaySub(String beginDateStr, String endDateStr) {

        long day = 0;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate;
        Date endDate;
        try {
            beginDate = format.parse(beginDateStr);
            endDate = format.parse(endDateStr);
            day = (endDate.getTime() - beginDate.getTime()) / (24 * 60 * 60 * 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return day;
    }

}