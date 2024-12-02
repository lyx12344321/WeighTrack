package io.github.weightrack.mapper;

import io.github.weightrack.module.PoundBillModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ShowListMapper {

    @Select("select * from poundbill where (IOType = #{IOType}) or (#{IOType} is null) order by creatTime desc")
    PoundBillModel[] showList(String IOType);
}