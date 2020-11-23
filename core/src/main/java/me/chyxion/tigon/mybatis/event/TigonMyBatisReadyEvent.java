package me.chyxion.tigon.mybatis.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationContext;

/**
 * @author Donghuang
 * @date Nov 23, 2020 21:28:02
 */
public class TigonMyBatisReadyEvent extends ApplicationEvent {
    private static final long serialVersionUID = 1L;

    @Getter
    private final ApplicationContext context;

    /**
     * Create a new {@link TigonMyBatisReadyEvent} instance.
     *
     * @param context the context that was being created
     */
    public TigonMyBatisReadyEvent(final ApplicationContext context) {
        super(context);
        this.context = context;
    }
}
