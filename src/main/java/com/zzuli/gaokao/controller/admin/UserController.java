package com.zzuli.gaokao.controller.admin;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzuli.gaokao.Utils.CommonUtils;
import com.zzuli.gaokao.Utils.HeaderUtil;
import com.zzuli.gaokao.bean.User;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.service.Impl.UserServiceImpl;
import com.zzuli.gaokao.vo.TfIdfVo;
import it.unimi.dsi.fastutil.Hash;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/user")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @Value("${gaokao.path.upload-path}")
    String uploadPath;

    @Value("${gaokao.path.domain}")
    String domain;


    /*
     * @Description: 实现用户的添加功能
     * @Date:   2024/3/31 21:38
     * @Param:  [user]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @PostMapping("/add")
    public Result saveUser(@RequestBody User user){

        if (user == null){
            return Result.error("请求参数为空！");
        }
        String username = user.getUsername();
        String email = user.getEmail();
        String password = user.getPassword();
        Integer provinceId = user.getProvinceId();
        String nickname = user.getNickname();
        String roles = user.getRoles();
        if(StringUtils.isBlank(username)){
            return Result.error("用户名不能为空！");
        }
        if(StringUtils.isBlank(password)){
            return Result.error("密码不能为空！");
        }
        if(StringUtils.isBlank(email)){
            return Result.error("邮箱不能为空！");
        }
        if(StringUtils.isBlank(roles)){
            return Result.error("角色不能为空！");
        }
        if(StringUtils.isBlank(nickname)){
            return Result.error("昵称不能为空！");
        }
        if(provinceId == null){
            return Result.error("省份不能为空！");
        }
        User one = null;
        one = userService.getOne(new QueryWrapper<User>().select("username").eq("username", username));
        if(one != null)
            return Result.error("用户名重复了,换一个吧");

        one = userService.getOne(new QueryWrapper<User>().select("email").eq("email",user.getEmail()));
        if(one != null)
            return Result.error("邮箱已经重复了,换一个吧");

        one = userService.getOne(new QueryWrapper<User>().select("nickname").eq("nickname",user.getNickname()));
        if(one != null)
            return Result.error("昵称已经重复了,换一个吧");
        String filename = CommonUtils.generateUUID();
        HeaderUtil.generate(user.getNickname(),uploadPath,filename);
        String headerUrl = domain + filename + ".jpg";
        user.setSalt(CommonUtils.generateUUID().substring(0,6));
        user.setPassword(CommonUtils.md5(user.getPassword() + user.getSalt()));
        user.setStatus(1);
        user.setCreateTime(new Date());
        user.setHeaderUrl(headerUrl);
        user.setDescription("这个人很懒，什么都没有写。");
        userService.save(user);
        return Result.success("用户创建成功！");
    }

    /*
     * @Description: 根据id更新用户
     * @Date:   2024/3/25 18:29
     * @Param:  [id, user]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @GetMapping("/update/{id}")
    public Result updateUser(@PathVariable Integer id,String username,String nickname,String password,String email,Integer provinceId,String roles){
        User one  = null;
        one = userService.getOne(new QueryWrapper<User>().eq("username", username));
        if(one != null && !one.getId().equals(id)){
            return Result.error("用户名重复了,换一个吧");
        }
        one = userService.getOne(new QueryWrapper<User>().eq("email",email));
        if(one != null  && !one.getId().equals(id)){
            return Result.error("该邮箱已经被占用了!");
        }
        one = userService.getOne(new QueryWrapper<User>().eq("nickname",nickname));
        if(one != null && !one.getId().equals(id)){
            return Result.error("该昵称已经被占用!");
        }
        User user = new User();
        if(!StringUtils.isBlank(username)){
            user.setUsername(username);
        }
        if(!StringUtils.isBlank(nickname)){
            user.setNickname(nickname);
        }
        if(!StringUtils.isBlank(email)){
            user.setEmail(email);
        }
        if(provinceId != null){
            user.setProvinceId(provinceId);
        }
        if(!StringUtils.isBlank(roles)){
            user.setRoles(roles);
        }
        if (!StringUtils.isBlank(password)){
            User tem = userService.getOne(new QueryWrapper<User>().select("salt").eq("id", id));
            String salt = tem.getSalt();
            user.setPassword(CommonUtils.md5(password+salt));
        }
        user.setId(id);
        userService.updateById(user);
        return Result.success("更新成功！");
    }

    /*
     * @Description: size表示当前页所显示的条数，page表示当前页码
     * @Date:   2024/3/25 17:55
     * @Param:  [size, page]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @GetMapping("/getPagination")
    public Result getUserList(Integer size,Integer page,String nickname,String username){
        System.out.println("用户分页方法");
        Page<User> pages = new Page<>(page, size);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if(nickname != null){
            wrapper.like("nickname",nickname);
        }
        if(username != null){
            wrapper.like("username",username);
        }
        HashMap<String, Object> map = new HashMap<>();
        Page<User> userpage = userService.page(pages, wrapper);
        List<User> list = userpage.getRecords();
        long total = userpage.getTotal();
        map.put("list",list);
        map.put("total",total);
        return  Result.success(map);
    }

    /*
     * @Description: 根据id 删除用户
     * @Date:   2024/3/25 18:21
     * @Param:  [id]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @GetMapping("/delete/{id}")
    public Result deleteUser(@PathVariable("id") Integer id){
        userService.removeById(id);
        return Result.success("成功删除！");
    }

    @GetMapping("/{id}")
    public Result getUserById(@PathVariable("id") Integer id){
        if(id == null){
            return Result.error("用户id不能为空！");
        }
        User user = userService.getOne(new QueryWrapper<User>().select("id","username", "nickname", "email", "province_id", "roles").eq("id", id));
        user.setPassword("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("user",user);
        return Result.success(map);
    }


    @PostMapping("/updateProfile")
    public Result updateProfile(Integer userId, @RequestBody ArrayList<TfIdfVo> tfIdfVos){
        if(userId == null){
            return Result.error("参数错误！");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;
        try {
             json = objectMapper.writeValueAsString(tfIdfVos);
        } catch (JsonProcessingException e) {
            log.error("json写入失败！");
        }
        userService.update(new UpdateWrapper<User>()
                .eq("id",userId)
                .set("profile",json));

        return Result.success("更新成功！");
    }

    @GetMapping("/getUserProfile")
    public Result getProfile(Integer userId){

        if(userId == null){
            return Result.error("参数错误！");
        }
        User one = userService.getOne(new QueryWrapper<User>()
                .eq("id", userId)
                .select("profile"));
        HashMap<String, Object> map = new HashMap<>();
        if(one != null){
            String profile = one.getProfile();

            ObjectMapper objectMapper = new ObjectMapper();
            ArrayList<TfIdfVo> arrayList = null;
            try {
                arrayList = objectMapper.readValue(profile, new TypeReference<ArrayList<TfIdfVo>>() {});
            } catch (JsonProcessingException e) {
                log.error("json解析失败！");
            }

            map.put("userProfile",arrayList);
        }else {
            map.put("userProfile",null);

        }

        return Result.success(map);
    }


}
