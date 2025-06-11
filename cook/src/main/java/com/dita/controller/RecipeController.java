/*package com.dita.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dita.domain.Recipe;
import com.dita.persistence.RecipeRepository;

@Controller 
public class RecipeController {

    @Autowired
    private RecipeRepository recipeRepository;

    @GetMapping("/recipe/detail")
    public String getRecipeDetail(@RequestParam("recipe_id") Integer recipeId, Model model) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("레시피 없음"));
        model.addAttribute("recipe", recipe);
        return "recipe_detail"; // => templates/recipe_detail.html
    }
}
*/

package com.dita.controller;

import com.dita.domain.Comment;
import com.dita.domain.Member;
import com.dita.domain.Recipe;
import com.dita.domain.RecipeIngredient;
import com.dita.domain.RecipeStep;
import com.dita.domain.RecipeSub;
import com.dita.domain.Reply;
import com.dita.persistence.CommentRepository;
import com.dita.persistence.MemberRepository;
import com.dita.persistence.MemberSubRepository;
import com.dita.persistence.RecipeIngredientRepository;
import com.dita.persistence.RecipeRepository;
import com.dita.persistence.RecipeStepRepository;
import com.dita.persistence.RecipeSubRepository;
import com.dita.persistence.ReplyRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class RecipeController {

	private final CategoryController categoryController;

	@Autowired
	private RecipeRepository recipeRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private RecipeSubRepository recipeSubRepository;

	@Autowired
	private RecipeIngredientRepository recipeIngredientRepo;

	@Autowired
	private RecipeStepRepository stepRepository;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private MemberSubRepository memsubRepo;
	
	@Autowired
	private ReplyRepository replyRepository;

	RecipeController(CategoryController categoryController) {
		this.categoryController = categoryController;
	}

	// 레시피 상세 페이지
	@GetMapping("/recipe_detail")
	public String showRecipeDetail(@RequestParam("recipe_id") Integer recipeId, Model model,
			@RequestParam(name = "showAll", required = false, defaultValue = "false") boolean showAll,
			HttpSession session, RedirectAttributes redirectAttributes) {

		recipeRepository.incrementViews(recipeId);
		Optional<Recipe> optionalRecipe = recipeRepository.findById(recipeId);

		// /////////////// 로그인 유저 /////////////////
		String loginId = (String) session.getAttribute("idKey");
		model.addAttribute("loginId", loginId);
		
		if (optionalRecipe.isEmpty()) {
			// 레시피가 없을 경우 기본 리디렉션
			return "redirect:/category";
		}

		Recipe recipe = optionalRecipe.get();
		model.addAttribute("recipe", recipe);

		// ////////// 좋아요 정보 //////////////
	    boolean liked = false;
	    
		Long likeCount = recipeSubRepository.countLiked(recipeId, 1); // 👍
		model.addAttribute("likeCount", likeCount);
		
		if (loginId != null) {
		    liked = recipeSubRepository
		                .findTop1ByUserIdAndRecipeIdAndStateOrderByRecipeSubDateDesc(loginId, recipeId, 1)
		                .isPresent();
		}

	    
	    model.addAttribute("liked", liked);

		// //////// 재료 정보 불러오기 /////////////
		List<RecipeIngredient> ingredients = recipeIngredientRepo
				.findByRecipeIdOrderByRecipeIngredientGroupIdAscRecipeIngredientIdAsc(recipeId);

		// //////// groupName별로 묶기 ////////
		Map<String, List<Map<String, String>>> groupedIngredients = new LinkedHashMap<>();
		for (RecipeIngredient ing : ingredients) {
			String group = ing.getGroupName();
			groupedIngredients.computeIfAbsent(group, k -> new ArrayList<>()).add(Map.of("ingredient_name",
					ing.getIngredientName(), "quantity1", ing.getQuantity1() != null ? ing.getQuantity1() : ""));
		}
		model.addAttribute("groupedIngredients", groupedIngredients);

		// ////// 동영상 추가 //////////
		String youtubeUrl = recipe.getVideosUrl();
		String embedUrl = "";
		if (youtubeUrl != null && !youtubeUrl.isEmpty()) {
			if (youtubeUrl.contains("youtube.com/shorts/")) {
				String videoId = youtubeUrl.substring(youtubeUrl.lastIndexOf("/") + 1);
				embedUrl = "https://www.youtube.com/embed/" + videoId;
			} else if (youtubeUrl.contains("youtube.com/watch?v=")) {
				embedUrl = youtubeUrl.replace("watch?v=", "embed/");
			} else if (youtubeUrl.contains("youtu.be/")) {
				String videoId = youtubeUrl.substring(youtubeUrl.lastIndexOf("/") + 1);
				embedUrl = "https://www.youtube.com/embed/" + videoId;
			} else {
				embedUrl = youtubeUrl;
			}

			embedUrl = embedUrl.replace("http://", "https://");
		}
		model.addAttribute("embedUrl", embedUrl);

		// ///// 레시피 순서 /////////
		List<RecipeStep> steps = stepRepository.findByRecipeIdOrderByStepId(recipeId);

		model.addAttribute("steps", steps);

		// /////// 레시피 태그 ///////////////
		if (recipe.getTags() != null) {
			List<String> tagList = Arrays.stream(recipe.getTags().split(",")).map(String::trim)
					.collect(Collectors.toList());
			model.addAttribute("tagList", tagList);
		}

		// ///////// 댓글 //////////

		// ✅ 전체보기 눌렀을 경우 → 댓글 전체 가져오고 → 파라미터 제거를 위해 redirect
		if (showAll) {
			session.setAttribute("showAllTemp", true);
			return "redirect:/recipe_detail?recipe_id=" + recipeId;
		}

		// ✅ 세션에 남은 값이 있으면 사용
		boolean showAllFinal = Boolean.TRUE.equals(session.getAttribute("showAllTemp"));
		if (showAllFinal)
			session.removeAttribute("showAllTemp");

		List<Comment> allComments = commentRepository.findByRecipeIdOrderByCommentCreateDesc(recipeId);
		List<Comment> commentsToShow = showAllFinal ? allComments : allComments.stream().limit(3).toList();

		
		// ///////////// 댓글 작성자 정보 가져오기
		List<Map<String, Object>> commentList = new ArrayList<>();

		for (Comment comment : commentsToShow) {
			Map<String, Object> map = new HashMap<>();
			map.put("comment", comment);
			
			List<Reply> replies = replyRepository.findByComment(comment);
			List<Map<String, Object>> replyList = new ArrayList<>();

			String userId = comment.getUserId();
			String profile = "default.jpg"; // 기본 이미지

			if (userId != null) {
				Optional<Member> commentMember = memberRepository.findById(userId);
				if (commentMember.isPresent() && commentMember.get().getProfile() != null) {
					profile = commentMember.get().getProfile();
				}
			}
			map.put("profile", profile);
			
			for (Reply reply : replies) {
		        Map<String, Object> replyMap = new HashMap<>();
		        replyMap.put("reply", reply);

		        String replyUserId = reply.getUserId();
		        String replyProfile = "default.jpg";

		        if (replyUserId != null) {
		            Optional<Member> replyMember = memberRepository.findById(replyUserId);
		            if (replyMember.isPresent() && replyMember.get().getProfile() != null) {
		                replyProfile = replyMember.get().getProfile();
		            }
		        }

		        replyMap.put("profile", replyProfile);
		        replyList.add(replyMap);
		    }
			
            map.put("replies", replyList);
			commentList.add(map);
		}
		
		int totalReplyCount = 0;
		for (Comment comment : allComments) {
		    totalReplyCount += replyRepository.countByComment(comment); // 각 댓글에 달린 답글 수
		}

		int totalCommentAndReplyCount = allComments.size() + totalReplyCount;
		model.addAttribute("totalVisibleCount", totalCommentAndReplyCount);

		model.addAttribute("commentList", commentList);

		model.addAttribute("showAll", showAllFinal);
		model.addAttribute("recipeId", recipeId);
		model.addAttribute("sessionUser", session.getAttribute("idKey"));

		// ///////////// 회원 프로필 가져오기 ////////
		Optional<Member> optionalMember = memberRepository.findByUserId(recipe.getUserId());

		if (optionalMember.isPresent()) {
			model.addAttribute("member", optionalMember.get());
		} else {
			model.addAttribute("member", null);
		}

		// ////////////////// 구독 버튼 클릭 처리 ///////////////////

		String recipeWriterId = recipe.getUserId(); // 작성자
		model.addAttribute("userId", recipeWriterId);

		boolean isSubscribed = memsubRepo.existsBySubUserAndSubedUserAndState(loginId, recipeWriterId, 1);

		model.addAttribute("btnLabel", isSubscribed ? "구독 중" : "구독");
		model.addAttribute("btnClass", isSubscribed ? "btn-subscribed" : "btn-unsubscribed");

		// ////////////////////////////
		
		// 작성자의 총 레시피 수
	    int recipeCount = recipeRepository.countByUserId(recipeWriterId);
	    
	    // 등급 계산
	    String gradeClass = "";
	    String gradeName = "";

	    if (recipeCount >= 10) {
	        gradeClass = "rank-master";
	        gradeName = "마스터 셰프";
	    } else if (recipeCount >= 5) {
	        gradeClass = "rank-advanced";
	        gradeName = "요리 장인";
	    } else if (recipeCount >= 3) {
	        gradeClass = "rank-intermediate";
	        gradeName = "중급 요리사";
	    } else if (recipeCount >= 2) {
	        gradeClass = "rank-novice";
	        gradeName = "초보 요리사";
	    } else {
	        gradeClass = "rank-beginner";
	        gradeName = "요리 견습생";
	    }
	    
	    model.addAttribute("gradeClass", gradeClass);
	    model.addAttribute("gradeName", gradeName);
	    
		
		
		return "recipe_detail"; // templates/recipe_detail.html 로 이동
	}

	// ///////////// 조회수 증가 ////////////
	@PostMapping("/updateViews")
	@ResponseBody
	public int updateViews(@RequestParam("recipeId") int recipeId) {
		recipeRepository.incrementViews(recipeId);
		return recipeRepository.findById(recipeId).get().getViews(); // 증가된 값 반환
	}

	// ////////////////// 댓글 가져오기 ///////////////
	@GetMapping("/recipe_detail/comments")
	@ResponseBody
	public List<Comment> getAllComments(@RequestParam("recipe_id") int recipeId) {
		return commentRepository.findByRecipeIdOrderByCommentCreateDesc(recipeId);
	}

	// ///////////// 댓글 폼 ////////////
	@PostMapping("/comment/write")
	public String writeComment(@RequestParam("recipeId") int recipeId, @RequestParam("comment") String comment,
			@RequestParam(value = "commentImage", required = false) MultipartFile file, HttpSession session,
			HttpServletRequest request) {

		String userId = (String) session.getAttribute("idKey");

		// 업로드 처리
		String fileName = null;
		if (!file.isEmpty()) {
			try {
				// 저장 폴더 경로 (ex: webapp/upload 또는 static/upload)
				String uploadDir = request.getServletContext().getRealPath("/upload/");
				File dir = new File(uploadDir);
				if (!dir.exists()) {
					dir.mkdirs();
				}

				// 고유 파일 이름 생성
				fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

				// 실제 파일 저장
				file.transferTo(new File(uploadDir + File.separator + fileName));
			} catch (IOException e) {
				e.printStackTrace();
				// 실패 시 에러 페이지로 이동하거나 처리
				return "redirect:/recipe_detail?recipe_id=" + recipeId;
			}

		}

		Comment newComment = new Comment();
		newComment.setUserId(userId);
		newComment.setRecipeId(recipeId);
		newComment.setComment(comment);
		newComment.setCommentCreate(new Timestamp(System.currentTimeMillis()));
		newComment.setCommentImage(fileName);

		commentRepository.save(newComment);

		// /////////////////////////////////////////

		return "redirect:/recipe_detail?recipe_id=" + recipeId;
	}
	
	// ////////////// 댓 삭제 ////////////////
	@PostMapping("/comment_delete")
    public String deleteComment(@RequestParam("comment_id") Long commentId,
                                @RequestParam("recipe_id") Integer recipeId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        String sessionUser = (String) session.getAttribute("idKey");

        Optional<Comment> optionalComment = commentRepository.findById(commentId);

        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();

            // 본인만 삭제 가능
            if (comment.getUserId().equals(sessionUser)) {
                commentRepository.deleteById(commentId);
                redirectAttributes.addFlashAttribute("msg", "댓글이 삭제되었습니다.");
            } else {
                redirectAttributes.addFlashAttribute("msg", "본인 댓글만 삭제할 수 있습니다.");
            }
        } else {
            redirectAttributes.addFlashAttribute("msg", "댓글이 존재하지 않습니다.");
        }

        return "redirect:/recipe_detail?recipe_id=" + recipeId;
    }
	
	// //////////////// 답글 삭제 ////////////////
	@PostMapping("/reply_delete")
	public String deleteReply(@RequestParam("reply_id") Integer replyId,
	                          @RequestParam("recipe_id") Integer recipeId,
	                          RedirectAttributes redirectAttributes) {
	    try {
	        replyRepository.deleteById(replyId);
	        redirectAttributes.addFlashAttribute("msg", "답글이 삭제되었습니다.");
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("msg", "삭제 중 오류가 발생했습니다.");
	    }
	    return "redirect:/recipe_detail?recipe_id=" + recipeId;
	}

}
