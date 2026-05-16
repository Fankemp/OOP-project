package university.user;

import university.enums.RequestStatus;
import university.communications.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TechSupportSpecialist extends Employee {

    private List<Request> assignedRequests;

    public TechSupportSpecialist(String id, String firstName, String lastName,
                                 String email, String login, String password,
                                 double salary) {
        super(id, firstName, lastName, email, login, password, salary);
        this.assignedRequests = new ArrayList<>();
    }

    /**
     * Displays all NEW requests from the provided queue and marks each
     * one as VIEWED as it is read — simulating the real "opening a ticket" flow.
     *
     * @param requestQueue the shared queue of all incoming requests
     * @return the list of requests that were in NEW status (now VIEWED)
     */
    public List<Request> viewNewRequests(List<Request> requestQueue) {
        List<Request> newRequests = requestQueue.stream()
                .filter(r -> r.getStatus() == RequestStatus.NEW)
                .collect(Collectors.toList());

        if (newRequests.isEmpty()) {
            System.out.printf("[TechSupport %s] No new requests.%n", getFullName());
        } else {
            System.out.printf("[TechSupport %s] === New Requests ===%n", getFullName());
            for (Request r : newRequests) {
                r.updateStatus(RequestStatus.VIEWED);
                System.out.printf("  [%s] %s — \"%s\" (submitted: %s)%n",
                        r.getStatus(), r.getId(), r.getDescription(), r.getCreatedAt());
            }
        }
        return newRequests;
    }

    /**
     * Accepts a request: status → ACCEPTED, request is added to this
     * specialist's personal assignment list.
     */
    public void acceptRequest(Request request) {
        validateRequest(request);
        request.updateStatus(RequestStatus.ACCEPTED);
        if (!assignedRequests.contains(request)) {
            assignedRequests.add(request);
        }
        System.out.printf("[TechSupport %s] Accepted request '%s': \"%s\"%n",
                getFullName(), request.getId(), request.getDescription());
    }

    /**
     * Rejects a request: status → REJECTED.
     * The request is NOT added to the specialist's assignment list.
     *
     * @param reason optional reason for rejection (can be null)
     */
    public void rejectRequest(Request request, String reason) {
        validateRequest(request);
        request.updateStatus(RequestStatus.REJECTED);
        String msg = (reason != null && !reason.isBlank()) ? " Reason: " + reason : "";
        System.out.printf("[TechSupport %s] Rejected request '%s'.%s%n",
                getFullName(), request.getId(), msg);
    }

    /** Overload without reason. */
    public void rejectRequest(Request request) {
        rejectRequest(request, null);
    }

    /**
     * Marks an accepted request as DONE once the issue is resolved.
     */
    public void markDone(Request request) {
        validateRequest(request);
        if (request.getStatus() != RequestStatus.ACCEPTED) {
            System.out.printf("[TechSupport %s] Cannot mark request '%s' as DONE — current status is %s.%n",
                    getFullName(), request.getId(), request.getStatus());
            return;
        }
        request.updateStatus(RequestStatus.DONE);
        System.out.printf("[TechSupport %s] Marked request '%s' as DONE.%n",
                getFullName(), request.getId());
    }

    private void validateRequest(Request request) {
        if (request == null) throw new IllegalArgumentException("Request cannot be null.");
    }

    /** Returns a defensive copy of the assigned-requests list. */
    public List<Request> getAssignedRequests() {
        return new ArrayList<>(assignedRequests);
    }

    @Override
    public String toString() {
        return String.format("TechSupportSpecialist{id='%s', name='%s', assignedRequests=%d}",
                getId(), getFullName(), assignedRequests.size());
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}