<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Hello World!</title>
</head>
<body>

<#-- list 数据的展示 -->
<b>展示list中的stu数据:</b>
<br>
<br>
<table>
    <tr>
        <td>序号</td>
        <td>姓名</td>
        <td>年龄</td>
        <td>钱包</td>
    </tr>
    <#list stus as stu>
        <#if stu.name == "小红">
            <tr style="color: red">
                <td>${stu_index + 1}</td>
                <td>${stu.name}</td>
                <td>${stu.age}</td>
                <td>${stu.money}</td>
            </tr>
        <#else >
                <tr>
                    <td>${stu_index + 1}</td>
                    <td>${stu.name}</td>
                    <td>${stu.age}</td>
                    <td>${stu.money}</td>
                </tr>
        </#if>
    </#list>
</table>
<hr>

<#-- Map 数据的展示 -->
<b>map数据的展示：</b>
<br/><br/>
<a href="###">方式一：通过map['keyname'].property</a><br/>
输出stu1的学生信息：<br/>
<#--姓名：${stusMap["stu1"].name}<br/>-->
<#--年龄：${stusMap["stu1"].age}<br/>-->
<br/>
<a href="###">方式二：通过map.keyname.property</a><br/>
输出stu2的学生信息：<br/>
<#--姓名：${stusMap.stu1.name}<br/>-->
<#--年龄：${stusMap.stu2.age}<br/>-->

<br/>
<a href="###">遍历map中两个学生信息：</a><br/>
<table>
    <tr>
        <td>序号</td>
        <td>姓名</td>
        <td>年龄</td>
        <td>钱包</td>
    </tr>
<#--    <#list stusMap?keys as key>-->
<#--        <tr>-->
<#--            <td>${key_index + 1}</td>-->
<#--            <td>${stusMap[key].name}</td>-->
<#--            <td>${stusMap[key].age}</td>-->
<#--            <td>${stusMap[key].money}</td>-->
<#--        </tr>-->
<#--    </#list>-->
</table>
<hr>

</body>
</html>