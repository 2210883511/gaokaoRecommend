package com.zzuli.gaokao.controller.api;

import com.alibaba.druid.sql.ast.SQLPartitionValue;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wf.captcha.SpecCaptcha;
import com.zzuli.gaokao.Utils.*;
import com.zzuli.gaokao.annotation.FilterWords;
import com.zzuli.gaokao.annotation.LoginRequired;
import com.zzuli.gaokao.bean.User;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.service.UserService;
import com.zzuli.gaokao.vo.TfIdfVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Host;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserHandler {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;


    @Value("${gaokao.path.upload-path}")
    String uploadPath;

    @Value("${gaokao.path.domain}")
    String domain;



    /*
     * @Description: 用户登录功能
     * @Date:   2024/5/10 20:17
     * @Param:  [user, captchaId, captcha]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @PostMapping("/login")
    public Result login(@RequestBody User user,String captchaId,String captcha){
        String username = user.getUsername();
        String password = user.getPassword();
        if(StringUtils.isBlank(username) || StringUtils.isBlank(password))
            return Result.error("用户名或密码为空！");

        if(StringUtils.isBlank(captcha))
            return Result.error("验证码为空！");
        String userCaptchaKey = RedisUtil.getUserCaptchaKey(captchaId);
        Object text = redisTemplate.opsForValue().get(userCaptchaKey);
        if(text == null){
            return Result.error("验证码过期！");
        }
        if(!captcha.equalsIgnoreCase(text.toString())){
            return Result.error("验证码错误！");
        }

        if(!userService.login(username,password)){
            return Result.error("用户名或密码错误");
        }
        /* 登录完成之后应该生成token并返回给前端，把token放到result中*/
        User one = userService.getOne(new QueryWrapper<User>()
                .select("id")
                .eq("username", username));
        HashMap<String, Object> userMap = new HashMap<>();
        Integer id = one.getId();
        userMap.put("id",id);
        userMap.put("username",username);
        String token = JwtUtil.getToken(userMap);
        String userTokenKey = RedisUtil.getUserTokenKey(username);
        redisTemplate.opsForValue().set(userTokenKey,token, CommonUtils.TOKEN_EXPIRED, TimeUnit.MILLISECONDS);
        HashMap<String, Object> map = new HashMap<>();
        map.put("userToken",token);
        return Result.success(map);
    }

    /*
     * @Description: 用户退出功能
     * @Date:   2024/5/10 20:23
     * @Param:  [token]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @LoginRequired
    @PostMapping("/logout")
    public Result logout(){
        User user = hostHolder.getUser();
        if(user == null){
            return Result.error("参数错误！");
        }
        String userTokenKey = RedisUtil.getUserTokenKey(user.getUsername());
        System.out.println(userTokenKey);
        redisTemplate.delete(userTokenKey);
        return Result.success("退出成功！");
    }





    
    /*
     * @Description: 根据用户token来获取用户信息
     * @Date:   2024/5/10 20:16
     * @Param:  [token]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @GetMapping("/userInfo")
    public Result userInfo(String token) {

        String subject = JwtUtil.getSubject(token);
        if(subject == null){
            log.warn("找不到该用户的信息");
            return Result.error("找不到该用户的信息");
        }
        User one = userService
                .getOne(new QueryWrapper<User>()
                        .select("username,nickname,roles,header_url,email,province_id,description,profile,id")
                        .eq("username",subject));
        HashMap<String, Object> userVo = new HashMap<>();
        String [] roles = {one.getRoles()};
        userVo.put("username",one.getUsername());
        userVo.put("nickname",one.getNickname());
        userVo.put("roles",roles);
        userVo.put("headerUrl",one.getHeaderUrl());
        userVo.put("email",one.getEmail());
        userVo.put("provinceId",one.getProvinceId());
        userVo.put("description",one.getDescription());
        userVo.put("id",one.getId());
        if(StringUtils.isNotBlank(one.getProfile())){
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                ArrayList arrayList = objectMapper.readValue(one.getProfile(), ArrayList.class);
                userVo.put("profile",arrayList);
            } catch (JsonProcessingException e) {
                log.warn("用户画像解析失败！{}",e.getMessage());
            }
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("user",userVo);
        return Result.success(map);
    }



    /*
     * @Description: 用户端生成验证码
     * @Date:   2024/5/10 20:16
     * @Param:  []
     * @Return: com.zzuli.gaokao.common.Result
     */
    @GetMapping("/captcha")
    public Result captcha()   {
        SpecCaptcha specCaptcha = new SpecCaptcha(100,40,4);
        String code = specCaptcha.text();
        String key = CommonUtils.generateUUID();
        String userCaptchaKey = RedisUtil.getUserCaptchaKey(key);
        redisTemplate.opsForValue().set(userCaptchaKey,code,CommonUtils.CAPTCHA_EXPIRED,TimeUnit.MINUTES);
        String base64 = specCaptcha.toBase64();
        HashMap<String, Object> map = new HashMap<>();
        map.put("id",key);
        map.put("url",base64);
        return Result.success(map);
    }


    @PostMapping("/registry")
    public Result register(@RequestBody User user,String captchaId,String captcha){
        if (user == null){
            return Result.error("请求参数为空！");
        }
        String userCaptchaKey = RedisUtil.getUserCaptchaKey(captchaId);
        Object text = redisTemplate.opsForValue().get(userCaptchaKey);
        if(text == null){
            return Result.error("验证码过期！");
        }
        if(!captcha.equalsIgnoreCase(text.toString())){
            return Result.error("验证码错误！");
        }
        String username = user.getUsername();
        String email = user.getEmail();
        String password = user.getPassword();
        Integer provinceId = user.getProvinceId();
        String nickname = user.getNickname();
        if(StringUtils.isBlank(username)){
            return Result.error("用户名不能为空！");
        }
        if(StringUtils.isBlank(password)){
            return Result.error("密码不能为空！");
        }
        if(StringUtils.isBlank(email)){
            return Result.error("邮箱不能为空！");
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
        return Result.success("添加成功！");
    }



    @LoginRequired
    @PostMapping("/upload")
    public Result upload(MultipartFile headerImg){

        if(headerImg == null){
            return Result.error("图片不能为空！");
        }
        String filename = headerImg.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        filename = CommonUtils.generateUUID() + suffix;
        File file = new File(uploadPath);
        if(!file.exists()){
            file.mkdirs();
        }
        try {
            headerImg.transferTo(new File(uploadPath + File.separator + filename));
            String headerUrl = domain + filename;
            HashMap<String, String> map = new HashMap<>();
            map.put("headerUrl",headerUrl);
            return Result.success(map);
        } catch (IOException e) {
            return Result.error("头像上传失败！！");
        }


    }



    @PostMapping("/update")
    @LoginRequired
    public Result updateUser(@RequestBody User user){
        User currentUser = hostHolder.getUser();
        Integer id = currentUser.getId();
        String email = user.getEmail();
        String password = user.getPassword();
        Integer provinceId = user.getProvinceId();
        String nickname = user.getNickname();
        if(StringUtils.isBlank(nickname)){
            return Result.error("昵称不能为空！");
        }
        if(StringUtils.isBlank(email)){
            return Result.error("邮箱不能为空！");
        }
        if(provinceId == null){
            return Result.error("省份不能为空！");
        }
        User one  = null;
        one = userService.getOne(new QueryWrapper<User>().eq("email",email));
        if(one != null  && !one.getId().equals(id)){
            return Result.error("该邮箱已经被占用了!");
        }
        one = userService.getOne(new QueryWrapper<User>().eq("nickname",nickname));
        if(one != null && !one.getId().equals(id)){
            return Result.error("该昵称已经被占用!");
        }

        if (!StringUtils.isBlank(password)){
            String salt = currentUser.getSalt();
            user.setPassword(CommonUtils.md5(password+salt));
        }
        user.setId(id);
        userService.updateById(user);
        return Result.success("更新成功！");
    }


    @LoginRequired
    @FilterWords
    @PostMapping("/updateProfile")
    public Result updateProfile(@RequestBody ArrayList<TfIdfVo> tfIdfVos){
        User user = hostHolder.getUser();
        Integer userId = user.getId();
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
    @LoginRequired
    @GetMapping("/getUserProfile")
    public Result getProfile(){
        User user = hostHolder.getUser();
        Integer userId = user.getId();
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
