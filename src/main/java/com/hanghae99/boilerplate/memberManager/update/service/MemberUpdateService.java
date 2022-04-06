package com.hanghae99.boilerplate.memberManager.update.service;

import com.hanghae99.boilerplate.memberManager.model.Member;
import com.hanghae99.boilerplate.memberManager.repository.MemberRepository;
import com.hanghae99.boilerplate.memberManager.update.UpdateMemberDto;
import com.hanghae99.boilerplate.security.model.MemberContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.sasl.AuthenticationException;

@Service
public class MemberUpdateService  {

    @Autowired
    MemberRepository memberRepository;
    @Transactional
    public MemberContext updateMemberInformation( MemberContext memberContext, UpdateMemberDto updateMemberDto) throws AuthenticationException {
        Member member= memberRepository.findById(memberContext.getMemberId()).orElseThrow(()->new AuthenticationException("bad access"));
        member.updateNicknameAndImage(updateMemberDto);
        return new MemberContext(member);
    }
    @Transactional
    public void deleteMember(MemberContext memberContext){
        memberRepository.deleteById(memberContext.getMemberId());
    }
}
