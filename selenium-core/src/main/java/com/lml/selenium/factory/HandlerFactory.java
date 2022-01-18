package com.lml.selenium.factory;

import cn.hutool.core.map.MapUtil;
import com.lml.selenium.enums.ActionEnum;
import com.lml.selenium.exception.InitException;
import com.lml.selenium.handler.SeleniumHandler;
import com.lml.selenium.handler.element.ElementHandler;
import com.lml.selenium.handler.other.OtherHandler;
import com.lml.selenium.util.ObjUtil;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author yugi
 * @apiNote 处理器的工厂类
 * @since 2019-04-30
 */
@UtilityClass
@Slf4j
@Getter
public class HandlerFactory {

    /**
     * 存放需要查找元素的处理器的map
     */
    private static final Map<ActionEnum, ElementHandler> ELEMENT_HANDLER = MapUtil.newHashMap();

    /**
     * 存放不需要查找元素的处理器的map
     */
    private static final Map<ActionEnum, OtherHandler> OTHER_HANDLER = MapUtil.newHashMap();


    /**
     * 初始化所有的处理器
     */
    public void initAllHandler() {
        initHandler(ElementHandler.class);
        initHandler(OtherHandler.class);
    }

    /**
     * 初始化对应的处理器
     *
     * @param cla 要初始化的超类
     */
    private void initHandler(Class<? extends SeleniumHandler> cla) {
        // 获取所有的实现类
        Set<Class<?>> classes = ObjUtil.getSubclass(cla);
        classes.stream().findFirst().ifPresent(tmp -> {
            try {
                SeleniumHandler tmpHandler = (SeleniumHandler) tmp.newInstance();
                if (tmpHandler.getAction().isNeedToFindDom()) {
                    setHandler(classes, eh -> ELEMENT_HANDLER.put(eh.getAction(), (ElementHandler) eh));
                }
                else {
                    setHandler(classes, eh -> OTHER_HANDLER.put(eh.getAction(), (OtherHandler) eh));
                }
                log.debug("===================初始化{}的子类完成===================", cla.getSimpleName());
            }
            catch (Exception e) {
                throw new InitException(e);
            }
        });
    }

    /**
     * 初始化对应的实现类并且放在map中
     *
     * @param classes  要初始化的类的列表
     * @param consumer 强转后的回调函数
     */
    private void setHandler(Set<Class<?>> classes, Consumer<SeleniumHandler> consumer) throws IllegalAccessException, InstantiationException {
        for (Class<?> clz : classes) {
            SeleniumHandler eh = (SeleniumHandler) clz.newInstance();
            consumer.accept(eh);
            log.debug("初始化{},成功", eh);
        }
    }


    /**
     * 获取ElementHandler
     *
     * @param actionEnum 对应的动作
     * @return {@link ElementHandler}
     */
    public ElementHandler getElementHandler(ActionEnum actionEnum) {
        return ELEMENT_HANDLER.get(actionEnum);
    }

    /**
     * 获取OtherHandler
     *
     * @param actionEnum 对应的动作
     * @return {@link OtherHandler}
     */
    public OtherHandler getOtherHandler(ActionEnum actionEnum) {
        return OTHER_HANDLER.get(actionEnum);
    }
}
