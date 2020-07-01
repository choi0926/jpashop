package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("api/v1/members")
    public List<Member> membersV1(){
        return memberService.findMembers();
    }

    @GetMapping("api/v2/members")
    public Result memberV2(){

        List<Member> findMembers = memberService.findMembers();
        List<MemberDao> collect = findMembers.stream().map(m -> new MemberDao(m.getName())).collect(Collectors.toList());
        return new Result(collect.size(),collect);
    }


    @Data
    @AllArgsConstructor
    static class Result<T>{
        private int count;
        private T data;
    }


    @Data
    @AllArgsConstructor
    static class MemberDao{
        private String name;
    }
    @PostMapping("/api/v1/members")
    public CreateResponse saveMember1(@RequestBody @Valid Member member){
        Long id = memberService.join(member);
        return new CreateResponse(id);
    }

    @PostMapping("api/v2/members")
    public CreateResponse saveMember2(@RequestBody @Valid CreateMemberRequest request){

        Member member = new Member();
        member.setName(request.getName());
        Long id = memberService.join(member);
        return new CreateResponse(id);
    }

    @PutMapping("api/v3/members/{id}")
    public UpdateResponse updateMember(@PathVariable("id") Long id, @RequestBody @Valid UpdateRequest request){
        memberService.update(id,request.getName());
        Member memberId = memberService.findOne(id);
        return new UpdateResponse(memberId.getId(),memberId.getName());
    }

    @Data
    @AllArgsConstructor
    static class UpdateResponse{
        private Long id;
        private String name;
    }

    @Data
    static  class UpdateRequest{
        private String name;
    }
    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
    }
    @Data
    static class CreateResponse{
        private Long id;
        private String name;

        public CreateResponse(Long id) {
            this.id = id;
        }
    }
}
