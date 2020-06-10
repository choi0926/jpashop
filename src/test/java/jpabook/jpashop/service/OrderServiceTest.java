package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.aspectj.weaver.ast.Or;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {
        //given
        Member member = createMember();
        Book book = createBook();

        int orderCount = 2 ;
        //when

        Long orderId = orderService.order(member.getId(),book.getId(),orderCount);

        
        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("상품주문시 상태는 ORDER", OrderStatus.ORDER,getOrder.getStatus());
        assertEquals("주문상품 종류 수", 1,getOrder.getOrderItems().size());
        assertEquals("주문가격은 수량 * 가격", 10000 * orderCount,getOrder.getTotalPrice());
        assertEquals("남은 수량", 8,book.getStockQuantity());
    }

    @Test
    public void 주문취소() throws Exception {
        //given
        Member member = createMember();
        Book book = createBook();
        int orderCount = 2 ;

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        //when

        orderService.cancelOrder(orderId);
        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("주문 취소시 상태는 CANCEL", OrderStatus.CANCEL,getOrder.getStatus());
        assertEquals("주문 취소된 수량만큼 재고증가 ",10,book.getStockQuantity());


    }
    @Test(expected = NotEnoughStockException.class)
    public void 상품주문_재고수량_초과() throws Exception {
        //given
        Member member = createMember();
        Book book = createBook();

        int orderCount = 11;
        
        //when
        orderService.order(member.getId(),book.getId(),orderCount);
        
        //then
        fail("재고 수량 부족 예외 발생해야한다.");
        
    }

    private Book createBook() {
        Book book = new Book();
        book.setName("JPA");
        book.setPrice(10000);
        book.setStockQuantity(10);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울","강가","123-123"));
        em.persist(member);
        return member;
    }
}