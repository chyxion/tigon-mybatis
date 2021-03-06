package me.chyxion.tigon.mybatis.test;

import lombok.val;
import java.util.*;
import lombok.var;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import lombok.extern.slf4j.Slf4j;
import me.chyxion.tigon.mybatis.Search;
import org.springframework.util.Assert;
import me.chyxion.tigon.mybatis.TestDriver;
import me.chyxion.tigon.mybatis.entity.User;
import org.apache.commons.lang3.RandomUtils;
import me.chyxion.tigon.mybatis.util.StrUtils;
import me.chyxion.tigon.mybatis.mapper.UserMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

/**
 * @author Donghuang
 * @date Sep 03, 2020 14:37:38
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TestDriver.class)
public class UserMapperTest extends AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    private UserMapper mapper;
    private final int testCaseSize = 17;
    private final String donghuang = "donghuang";

    @Before
    public void setup() {

        val userList = new ArrayList<User>(10);

        // birth date from 1980 to 2000
        val dateFrom = Calendar.getInstance();
        dateFrom.set(1980, 1, 1, 0, 0);
        val dateTo = Calendar.getInstance();
        dateTo.set(2000, 1, 1, 0, 0);

        for (int i = 0; i < testCaseSize - 1; ++i) {
            val user = new User();
            user.setName("User " + i);
            user.setAccount("account" + i);
            user.setMobile("1376" + RandomStringUtils.randomNumeric(7));
            user.setPassword(RandomStringUtils.randomAlphanumeric(16));
            user.setGender(RandomUtils.nextInt(0, 2) > 0 ?
                User.Gender.MALE : User.Gender.FEMALE);

            user.setBirthDate(new Date(
                RandomUtils.nextLong(dateFrom.getTimeInMillis(),
                    dateTo.getTimeInMillis())));

            user.setCity(RandomUtils.nextInt(0, 2) > 0 ?
                "Hangzhou" : "Shanghai");

            user.setActive(true);
            user.setRemark("Init remark");
            user.setCreatedBy(donghuang);
            user.setCreatedAt(new Date());
            userList.add(user);
        }
        mapper.insert(userList);

        val user = new User();
        user.setName(StrUtils.capitalize(donghuang));
        user.setAccount(donghuang);
        user.setMobile("1376" + RandomStringUtils.randomNumeric(7));
        user.setPassword(RandomStringUtils.randomAlphanumeric(16));
        user.setGender(User.Gender.MALE);
        user.setBirthDate(new Date(
            RandomUtils.nextLong(dateFrom.getTimeInMillis(),
                dateTo.getTimeInMillis())));
        user.setCity("Shanghai");

        user.setActive(true);
        user.setRemark("Uncle Donghuang");
        user.setCreatedBy("donghuang");
        user.setCreatedAt(new Date());
        mapper.insert(user);
        Assert.state(user.getId() != null,
            "Test @UseGeneratedKeys failed");
    }

    @Test
    public void testRun() {
        val userListFound = mapper.list(new Search());
        Assert.state(userListFound.size() == testCaseSize, "Test list failed");
        Assert.state(mapper.list(Arrays.asList(1, 2, 3)).size() == 3,
            "Test list by id collection failed");
        Assert.state(mapper.list(new Integer[]{1, 2, 3}).size() == 3,
            "Test list by id array failed");
        Assert.state(mapper.count(null) == testCaseSize, "Test count failed");
        Assert.state(mapper.count(new Search("account", donghuang)) == 1, "Test count failed");
        Assert.state(mapper.find(new Search("account", donghuang)).getAccount().equals(donghuang), "Test find failed");
        Assert.state(mapper.exists(new Search("account", donghuang)), "Test exists failed");

        for (val user : userListFound) {
            log.info("User [{}] found.", user);
            user.setUpdatedBy(donghuang);
            user.setUpdatedAt(new Date());
            mapper.update(user);

            val update = new HashMap<String, Object>();
            update.put("remark", user.getRemark() + " Updated");
            mapper.update(update, user.getId());
        }

        val userDonghuang = mapper.findByAccount(donghuang);
        Assert.state(userDonghuang != null, "Test find failed");
        Assert.state(userDonghuang.getAccessKey().length() == 16, "Test @RawValue failed");
        userDonghuang.setCity("Beijing");
        userDonghuang.setAccount("UpdateWillBeIgnored");
        mapper.update(userDonghuang);

        val account10Updated = mapper.find(userDonghuang.getId());
        Assert.state(userDonghuang.getCity().equals(
                account10Updated.getCity()),
            "Test update failed");
        Assert.state(donghuang.equals(account10Updated.getAccount()),
            "Account should not be updated");

        Assert.state(mapper.listByName("Donghuang").size() == 1, "Test listByName failed");

        val update = new HashMap<String, Object>();
        update.put("remark", "update remark id gt 3 and lt 6");
        val updatedBy = "donghuang.cxn";
        update.put("updatedBy", updatedBy);
        update.put("updatedAt", new Date());
        mapper.update(update, new Search().gt("id", 3).lt("id", 6));
        Assert.state(mapper.find(4).getUpdatedBy().equals(updatedBy),
            "Test update map failed");
        Assert.state(mapper.find(5).getUpdatedBy().equals(updatedBy),
            "Test update map failed");

        mapper.setNull("remark", new Search(3));
        var user3 = mapper.find(3);
        Assert.state(user3.getRemark() == null, "Test setNull failed");
        mapper.setNull(new String[] {"remark", "updatedBy"}, new Search(3));

        user3 = mapper.find(3);
        Assert.state(user3.getRemark() == null &&
            user3.getUpdatedBy() == null, "Test setNull failed");
        Assert.state(mapper.find(new Search(3)
                        .isNull("updatedBy")).getRemark() == null,
            "Test isNull failed");
        Assert.state(mapper.find(new Search(3)
                        .eq("updatedBy", null)).getRemark() == null,
            "Test eq NULL failed");
        Assert.state(mapper.find(
                new Search(10).notNull("updatedBy")).getRemark() != null,
            "Test notNull failed");
        Assert.state(mapper.find(
                new Search(10).ne("updatedBy", null)).getRemark() != null,
            "Test notNull failed");

        mapper.setNull("remark", new Search().between("id", 6, 8));

        final Integer id = mapper.findCol("id", new Search(1));
        Assert.state(Integer.valueOf(1).equals(id), "Test findCol failed");
        final List<Integer> idList = mapper.listCol("id", new Search(1));
        Assert.state(Integer.valueOf(1).equals(idList.get(0)), "Test listCol failed");

        val userList13 = mapper.list(new Search().build(
                arg -> arg.addSql("id % 2 in ").addParamList(1, 3)));
        for (val user : userList13) {
            val idMod2 = user.getId() % 2;
            Assert.state(idMod2 == 1 || idMod2 == 3, "Test Search#build failed");
        }

        val deleteId = 7;
        mapper.delete(deleteId);
        Assert.state(mapper.find(deleteId) == null, "Test delete failed");

        val deleteId2 = 9;
        mapper.delete(new Search(deleteId2));
        Assert.state(mapper.find(deleteId2) == null, "Test delete failed");
    }

    @Test
    public void testScan() {
        List<Integer> ids = Arrays.asList(10, 11, 13, 15);
        mapper.scan(3, new Search(ids), user -> {
            log.info("Scan user [{}].", user);
            Assert.state(ids.contains(user.getId()), "Test scan failed");
        });
    }
}
