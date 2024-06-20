package kr.ac.kopo.guestbook2024.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import kr.ac.kopo.guestbook2024.dto.GuestbookDTO;
import kr.ac.kopo.guestbook2024.dto.PageRequestDTO;
import kr.ac.kopo.guestbook2024.dto.PageResultDTO;
import kr.ac.kopo.guestbook2024.entity.Guestbook;
import kr.ac.kopo.guestbook2024.entity.QGuestbook;
import kr.ac.kopo.guestbook2024.repository.GuestbookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class GuestbookServiceImpl implements GuestbookService{

    private final GuestbookRepository repository; //fianl꼭 써야함
    @Override
    public Long register(GuestbookDTO dto){
        Guestbook entity = dtoToEntity(dto);

        repository.save(entity);
        return entity.getGno();
    }

    @Override
    public Guestbook dtoToEntity(GuestbookDTO dto) {
        return GuestbookService.super.dtoToEntity(dto);
    }

    @Override
    public PageResultDTO<GuestbookDTO, Guestbook> getList(PageRequestDTO requestDTO) {
        Pageable pageable = requestDTO.getPageable(Sort.by("gno").descending());
        BooleanBuilder booleanBuilder = getSearch(requestDTO);/*where절의 조건식*/
        Page<Guestbook> result = repository.findAll(booleanBuilder,pageable);/*조건식이 포함된 select문*/
        Function<Guestbook, GuestbookDTO> fn = (entity -> entityToDTO(entity));
        return new PageResultDTO<>(result,fn);
    }

    @Override
    public GuestbookDTO read(Long gno) {
        Optional<Guestbook> result = repository.findById(gno);
        return result.isPresent() ? entityToDTO(result.get()): null;
    }

    @Override
    public void modify(GuestbookDTO dto) {
        Optional<Guestbook> result = repository.findById(dto.getGno());

        if(result.isPresent()){
            Guestbook entity = result.get();

            entity.changeTitle(dto.getTitle());
            entity.changeContent(dto.getContent());

            repository.save(entity);//글의 제목과 내용을 update문 실행
        }
    }

    @Override
    public void remove(Long gno) {
        repository.deleteById(gno);
    }
    @Override
    public BooleanBuilder getSearch(PageRequestDTO requestDTO){
        String type = requestDTO.getType();
        String keyowrd = requestDTO.getKeyword();

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QGuestbook qGuestbook = QGuestbook.guestbook;

        BooleanExpression booleanExpression = qGuestbook.gno.gt(0L);

        booleanBuilder.and(booleanExpression);

        //화면에서 검색 조건을 선택하지 않은 경우 (검색 타입=null 및 검색어는 입력이 안된 경우)
        if(type == null || keyowrd.trim().length()==0 || type.trim().length()==0){
            return booleanBuilder;
        }

        BooleanBuilder conditionbuilder = new BooleanBuilder();
        if (type.contains("t")){
            conditionbuilder.or(qGuestbook.title.contains(keyowrd));
        }
        if (type.contains("c")){
            conditionbuilder.or(qGuestbook.content.contains(keyowrd));
        }
        if (type.contains("w")){
            conditionbuilder.or(qGuestbook.writer.contains(keyowrd));
        }

        booleanBuilder.and(conditionbuilder);
        return booleanBuilder;
    }
}